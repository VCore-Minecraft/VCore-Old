/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms.nmshandler.v1_16_3.world;

import de.verdox.vcore.concurrent.CatchingRunnable;
import de.verdox.vcore.util.VCoreUtil;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.nms.nmshandler.api.world.NMSWorldHandler;
import de.verdox.vcorepaper.nms.packetabstraction.wrapper.ChunkPacketWrapper;
import de.verdox.vcorepaper.nms.packetabstraction.wrapper.WorldBorderPacketWrapper;
import de.verdox.vcorepaper.nms.reflection.java.FieldReflection;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_16_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.checkerframework.checker.index.qual.NonNegative;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.06.2021 14:51
 */
public class WorldHandler_V1_16_R3 implements NMSWorldHandler {

    @Override
    public void resetView(Player player, Runnable callback) {
        VCorePaper.getInstance().createTaskBatch().doAsync(() -> {
            CraftPlayer craftPlayer = (CraftPlayer) player;
            CraftServer craftServer = (CraftServer) craftPlayer.getServer();
            DedicatedServer dedicatedServer = craftServer.getServer();
            dedicatedServer.getPlayerList().moveToWorld(craftPlayer.getHandle(), false);
        }).executeBatch(callback);
    }

    @Override
    public void refreshChunks(Player player, Runnable callback) {
        VCorePaper.getInstance().createTaskBatch().doAsync(() -> {
            VCoreUtil.getBukkitPlayerUtil().getChunksInServerViewDistance(player).forEach(chunkKey -> {
                org.bukkit.World world = player.getWorld();
                refreshChunk(player,chunkKey.getChunkIn(world));
            });
        }).executeBatch(callback);
    }

    @Override
    public void refreshChunk(@Nonnull Player player, @Nonnull Chunk chunk, Runnable callback) {
        VCorePaper.getInstance().createTaskBatch().doAsync(() -> {
            PacketPlayOutMapChunk packetPlayOutMapChunk = new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle(), 65535,true);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutMapChunk);
            VCorePaper.getInstance().consoleMessage("Sent Fake Chunk ["+chunk.getX()+"|"+chunk.getZ()+"]", true);
        }).executeBatch(callback);
    }

    @Override
    public void sendFakeBiome(@Nonnull Player player, @Nonnull Chunk chunk, @Nonnull Biome biome, Runnable callback) {
        VCorePaper.getInstance().createTaskBatch().doAsync(() -> {
            ChunkPacketWrapper.V_1_16_R3 chunkPacketWrapper = new ChunkPacketWrapper.V_1_16_R3(chunk, 65535,true);
            int[] biomeArray = chunkPacketWrapper.biomes.readField();
            Arrays.fill(biomeArray, VCoreUtil.getVanillaUtil().getBiomeID_1_16(biome));
            chunkPacketWrapper.biomes.setField(biomeArray);
            chunkPacketWrapper.sendPlayer(player);
        }).executeBatch(callback);
    }

    @Override
    public void sendFakeDimension(@Nonnull Player player, @Nonnull org.bukkit.World.Environment environment, Runnable callback) {
        Location location = player.getLocation().clone();
        boolean flag = !location.getWorld().getEnvironment().equals(environment);
        CraftPlayer craftPlayer = (CraftPlayer) player;
        CraftServer craftServer = (CraftServer) craftPlayer.getServer();
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        WorldServer worldServer = entityPlayer.getWorldServer();
        WorldData worldData = worldServer.getWorldData();
        DedicatedServer dedicatedServer = craftServer.getServer();
        long seed = craftPlayer.getWorld().getSeed();

        ResourceKey<World> world;
        DimensionManager dimensionManager;
        switch (environment){
            case NORMAL: {
                world = World.OVERWORLD;
                dimensionManager = FieldReflection.getField(DimensionManager.class,"OVERWORLD_IMPL",DimensionManager.class).readField();
                break;
            }
            case NETHER: {
                world = World.THE_NETHER;
                dimensionManager = FieldReflection.getField(DimensionManager.class,"THE_NETHER_IMPL",DimensionManager.class).readField();
                break;
            }
            case THE_END: {
                world = World.THE_END;
                dimensionManager = FieldReflection.getField(DimensionManager.class,"THE_END_IMPL",DimensionManager.class).readField();
                break;
            }
            default:throw new IllegalStateException("Unknwon Environment: "+environment);
        }

        if(world == null)
            return;

        VCorePaper.getInstance().createTaskBatch().doSync(new CatchingRunnable(() -> {
            worldServer.removePlayer(entityPlayer);
        })).doAsync(() -> {
            EnumGamemode enumGamemode = EnumGamemode.getById(player.getGameMode().getValue());
            craftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutRespawn(dimensionManager, world, seed, enumGamemode, enumGamemode, false, false, flag));
            craftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutViewDistance(worldServer.getChunkProvider().playerChunkMap.getLoadViewDistance()));
        }).doSync(() -> {
            entityPlayer.spawnIn(worldServer);
            entityPlayer.dead = false;
            entityPlayer.playerConnection.teleport(new Location(worldServer.getWorld(), entityPlayer.locX(), entityPlayer.locY(), entityPlayer.locZ(), entityPlayer.yaw, entityPlayer.pitch));
        }).doAsync(new CatchingRunnable(() -> {
            entityPlayer.playerConnection.sendPacket(new PacketPlayOutSpawnPosition(worldServer.getSpawn(), worldServer.v()));
            entityPlayer.playerConnection.sendPacket(new PacketPlayOutServerDifficulty(worldServer.getDifficulty(), worldData.isDifficultyLocked()));
            entityPlayer.playerConnection.sendPacket(new PacketPlayOutExperience(entityPlayer.exp, entityPlayer.expTotal, entityPlayer.expLevel));
            WorldBorder worldborder = entityPlayer.world.getWorldBorder();
            entityPlayer.playerConnection.sendPacket(new PacketPlayOutWorldBorder(worldborder, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE));
            dedicatedServer.getPlayerList().updateClient(entityPlayer);
            entityPlayer.updateAbilities();
        })).doSync(() -> {
            FieldReflection.ReferenceField<Map> entitiesByUUID  = FieldReflection.getField(WorldServer.class, "entitiesByUUID", Map.class).of(worldServer);
            Map<UUID, Entity> map = entitiesByUUID.readField();
            if(!map.containsKey(entityPlayer.getUniqueID()))
                worldServer.addPlayerRespawn(entityPlayer);
        }).executeBatch(callback);
    }

    @Override
    public void sendFakeWorldBorder(@Nonnull Player player, @Nonnull Location center, @NonNegative double size, Runnable callback) {
        WorldBorderPacketWrapper.V_1_16_R3 worldBorderPacketWrapper = new WorldBorderPacketWrapper.V_1_16_R3();
        VCorePaper.getInstance()
                .createTaskBatch()
                .doAsync(() -> {
                    CraftPlayer craftPlayer = (CraftPlayer) player;
                    double coordinateScale = craftPlayer.getHandle().getWorldServer().getDimensionManager().getCoordinateScale();
                    worldBorderPacketWrapper.x.setField(center.getX() * coordinateScale);
                    worldBorderPacketWrapper.z.setField(center.getZ() * coordinateScale);
                    worldBorderPacketWrapper.speed.setField(0L);
                    worldBorderPacketWrapper.warningBlocks.setField(0);
                    worldBorderPacketWrapper.warningTime.setField(0);
                    worldBorderPacketWrapper.size.setField(size);
                    worldBorderPacketWrapper.newSize.setField(size);
                    worldBorderPacketWrapper.worldBorderAction.setField(PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_SIZE);
                    worldBorderPacketWrapper.sendPlayer(player);
                }).wait(50L, TimeUnit.MILLISECONDS).doAsync(() -> {
            worldBorderPacketWrapper.worldBorderAction.setField(PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_CENTER);
            worldBorderPacketWrapper.sendPlayer(player);
        }).executeBatch(callback);
    }

    @Override
    public void refreshWorldBorder(@Nonnull Player player, Runnable callback) {
        VCorePaper.getInstance()
                .createTaskBatch()
                .doAsync(() -> {
                    CraftPlayer craftPlayer = (CraftPlayer) player;
                    PacketPlayOutWorldBorder packetPlayOutWorldBorder = new PacketPlayOutWorldBorder(craftPlayer.getHandle().getWorld().getWorldBorder(), PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE);
                    craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutWorldBorder);
                }).executeBatch(callback);
    }
}
