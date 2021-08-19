/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms.nmshandler.v1_16_3.world;

import de.verdox.vcore.performance.concurrent.CatchingRunnable;
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
import org.bukkit.boss.DragonBattle;
import org.bukkit.craftbukkit.v1_16_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.boss.CraftDragonBattle;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.checkerframework.checker.index.qual.NonNegative;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
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
            VCoreUtil.BukkitUtil.getBukkitPlayerUtil().getChunksInServerViewDistance(player).forEach(chunkKey -> {
                org.bukkit.World world = player.getWorld();
                CompletableFuture<Chunk> futureChunk = chunkKey.getChunkIn(world);
                if (futureChunk != null)
                    futureChunk.thenApply(chunk -> {
                        refreshChunk(player, chunk);
                        return true;
                    });
            });
        }).executeBatch(callback);
    }

    @Override
    public void refreshChunk(@Nonnull Player player, @Nonnull Chunk chunk, Runnable callback) {
        VCorePaper.getInstance().createTaskBatch().doAsync(() -> {
            PacketPlayOutMapChunk packetPlayOutMapChunk = new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle(), 65535, true);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutMapChunk);
            VCorePaper.getInstance().consoleMessage("Sent Fake Chunk [" + chunk.getX() + "|" + chunk.getZ() + "]", true);
        }).executeBatch(callback);
    }

    @Override
    public void sendFakeBiome(@Nonnull Player player, @Nonnull Chunk chunk, @Nonnull Biome biome, Runnable callback) {
        VCorePaper.getInstance().createTaskBatch().doAsync(() -> {
            ChunkPacketWrapper.V_1_16_R3 chunkPacketWrapper = new ChunkPacketWrapper.V_1_16_R3(chunk, 65535, true);
            int[] biomeArray = chunkPacketWrapper.biomes.readField();
            Arrays.fill(biomeArray, VCoreUtil.BukkitUtil.getVanillaUtil().getBiomeID_1_16(biome));
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
        ResourceKey<World> fakeWorld;
        DimensionManager dimensionManager;
        DimensionManager fakeDimensionManager;
        switch (environment) {
            case NORMAL: {
                world = World.OVERWORLD;
                dimensionManager = FieldReflection.getField(DimensionManager.class, "OVERWORLD_IMPL", DimensionManager.class).readField();

                fakeWorld = World.THE_NETHER;
                fakeDimensionManager = FieldReflection.getField(DimensionManager.class, "THE_NETHER_IMPL", DimensionManager.class).readField();
                break;
            }
            case NETHER: {
                world = World.THE_NETHER;
                dimensionManager = FieldReflection.getField(DimensionManager.class, "THE_NETHER_IMPL", DimensionManager.class).readField();

                fakeWorld = World.THE_END;
                fakeDimensionManager = FieldReflection.getField(DimensionManager.class, "THE_END_IMPL", DimensionManager.class).readField();
                break;
            }
            case THE_END: {
                world = World.THE_END;
                dimensionManager = FieldReflection.getField(DimensionManager.class, "THE_END_IMPL", DimensionManager.class).readField();

                fakeWorld = World.OVERWORLD;
                fakeDimensionManager = FieldReflection.getField(DimensionManager.class, "OVERWORLD_IMPL", DimensionManager.class).readField();
                break;

            }
            default:
                throw new IllegalStateException("Unknwon Environment: " + environment);
        }

        if (world == null)
            return;
        EnumGamemode enumGamemode = EnumGamemode.getById(player.getGameMode().getValue());
        VCorePaper.getInstance().createTaskBatch().doSync(new CatchingRunnable(() -> {
            //worldServer.removePlayer(entityPlayer);
        })).doAsync(() -> {
            //craftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutRespawn(fakeDimensionManager, world, seed, enumGamemode, enumGamemode, false, false, flag));
        }).wait(50, TimeUnit.MILLISECONDS).doAsync(() -> {
            //craftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutRespawn(fakeDimensionManager, fakeWorld, seed, enumGamemode, enumGamemode, false, false, flag));
            craftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutRespawn(dimensionManager, world, seed, enumGamemode, enumGamemode, false, false, flag));
            //craftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutViewDistance(craftPlayer.getWorld().getViewDistance()));
        }).doSync(() -> {
            //entityPlayer.spawnIn(worldServer);
            //entityPlayer.dead = false;
            //entityPlayer.playerConnection.teleport(new Location(worldServer.getWorld(), entityPlayer.locX(), entityPlayer.locY(), entityPlayer.locZ(), entityPlayer.yaw, entityPlayer.pitch));
        }).wait(200, TimeUnit.MILLISECONDS).doAsync(new CatchingRunnable(() -> {
            //entityPlayer.playerConnection.sendPacket(new PacketPlayOutSpawnPosition(worldServer.getSpawn(), worldServer.v()));
            //entityPlayer.playerConnection.sendPacket(new PacketPlayOutServerDifficulty(worldServer.getDifficulty(), worldData.isDifficultyLocked()));
            //entityPlayer.playerConnection.sendPacket(new PacketPlayOutExperience(entityPlayer.exp, entityPlayer.expTotal, entityPlayer.expLevel));
            WorldBorder worldborder = entityPlayer.world.getWorldBorder();
            //entityPlayer.playerConnection.sendPacket(new PacketPlayOutWorldBorder(worldborder, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE));
            //dedicatedServer.getPlayerList().updateClient(entityPlayer);
            //entityPlayer.updateAbilities();

        })).doSync(() -> {
            entityPlayer.spawnIn(worldServer);
            entityPlayer.dead = false;
            entityPlayer.playerConnection.teleport(location);
        });//.executeBatch(callback);

        //try {
        //    PacketContainer respawnPacket = PacketContainer.fromPacket(new PacketPlayOutRespawn(dimensionManager, world, seed, enumGamemode, enumGamemode, false, false, flag));
//
        //    int viewDistance = player.getWorld().getViewDistance();
//
        //    entityPlayer.playerConnection.sendPacket(new PacketPlayOutSpawnPosition(worldServer.getSpawn(), worldServer.v()));
        //    craftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutViewDistance(500));
//
        //    ProtocolLibrary.getProtocolManager().sendServerPacket(player,respawnPacket, true);
//
        //    worldServer.getChunkProvider().addTicket(TicketType.POST_TELEPORT, new ChunkCoordIntPair(location.getBlockX() >> 4, location.getBlockZ() >> 4), 1, entityPlayer.getId());
//
        //    //for (Chunk chunk : VCoreUtil.getBukkitPlayerUtil().getChunksAroundPlayer(player)) {
        //    //    PacketContainer chunkPacket = PacketContainer.fromPacket(new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle(), viewDistance,false));
        //    //    ProtocolLibrary.getProtocolManager().sendServerPacket(player,chunkPacket, true);
        //    //}
//
//
//
        //    dedicatedServer.getPlayerList().updateClient(entityPlayer);
        //    entityPlayer.updateAbilities();
//
//
        //    //entityPlayer.spawnIn(worldServer);
        //    //entityPlayer.dead = false;
        //    entityPlayer.playerConnection.teleport(location);
        //} catch (InvocationTargetException e) {
        //    e.printStackTrace();
        //}
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

    @Override
    public DragonBattle createDragonBattle(@Nonnull Location dragonSpawnLoc, @Nonnull Location exitPortalLoc) {
        WorldServer worldServer = ((CraftWorld) dragonSpawnLoc.getWorld()).getHandle();

        NBTTagCompound nbtTagCompound = new NBTTagCompound();

        EnderDragon enderDragon = (EnderDragon) dragonSpawnLoc.getWorld().spawnEntity(dragonSpawnLoc, EntityType.ENDER_DRAGON);

        nbtTagCompound.setUUID("Dragon", enderDragon.getUniqueId());
        nbtTagCompound.setBoolean("PreviouslyKilled", false);
        nbtTagCompound.setBoolean("DragonKilled", false);
        nbtTagCompound.setBoolean("IsRespawning", false);

        NBTTagCompound exitPortalLocation = nbtTagCompound.getCompound("ExitPortalLocation");
        exitPortalLocation.setInt("X", exitPortalLoc.getBlockX());
        exitPortalLocation.setInt("Y", exitPortalLoc.getBlockY());
        exitPortalLocation.setInt("Z", exitPortalLoc.getBlockZ());

        EnderDragonBattle enderDragonBattle = new EnderDragonBattle(worldServer, dragonSpawnLoc.getWorld().getSeed(), nbtTagCompound);
        return new CraftDragonBattle(enderDragonBattle);
    }
}
