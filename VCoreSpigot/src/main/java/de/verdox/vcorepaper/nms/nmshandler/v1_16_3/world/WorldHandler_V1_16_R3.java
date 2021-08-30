/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms.nmshandler.v1_16_3.world;

import com.mojang.datafixers.util.Pair;
import de.verdox.vcore.util.VCoreUtil;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.nms.nmshandler.api.world.NMSWorldHandler;
import de.verdox.vcorepaper.nms.packetabstraction.wrapper.ChunkPacketWrapper;
import de.verdox.vcorepaper.nms.packetabstraction.wrapper.WorldBorderPacketWrapper;
import de.verdox.vcorepaper.nms.reflection.java.FieldReflection;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_16_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftItemFrame;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.*;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
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
        CraftPlayer craftPlayer = (CraftPlayer) player;
        CraftServer craftServer = (CraftServer) craftPlayer.getServer();
        DedicatedServer dedicatedServer = craftServer.getServer();
        dedicatedServer.getPlayerList().moveToWorld(craftPlayer.getHandle(), false);
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
    public void refreshChunk(@NotNull Player player, @NotNull Chunk chunk, Runnable callback) {
        VCorePaper.getInstance().createTaskBatch().doAsync(() -> {
            PacketPlayOutMapChunk packetPlayOutMapChunk = new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle(), 65535, false);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutMapChunk);
            VCorePaper.getInstance().consoleMessage("Sent Fake Chunk [" + chunk.getX() + "|" + chunk.getZ() + "]", true);
        }).executeBatch(callback);
    }

    @Override
    public void sendFakeBiome(@NotNull Player player, @NotNull Chunk chunk, @NotNull Biome biome, Runnable callback) {
        VCorePaper.getInstance().createTaskBatch().doAsync(() -> {
            ChunkPacketWrapper.V_1_16_R3 chunkPacketWrapper = new ChunkPacketWrapper.V_1_16_R3(chunk, 65535, true);
            int[] biomeArray = chunkPacketWrapper.biomes.readField();
            Arrays.fill(biomeArray, VCoreUtil.BukkitUtil.getVanillaUtil().getBiomeID_1_16(biome));
            chunkPacketWrapper.biomes.setField(biomeArray);
            chunkPacketWrapper.sendPlayer(player);
        }).executeBatch(callback);
    }


    @Override
    public void sendFakeDimension(@NotNull Player player, @NotNull org.bukkit.World.Environment environment, Runnable callback) {
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

        VCorePaper.getInstance().async(() -> {
            EnumGamemode enumGamemode = EnumGamemode.getById(player.getGameMode().getValue());
            craftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutRespawn(dimensionManager, world, seed, enumGamemode, enumGamemode, false, false, flag));
            entityPlayer.playerConnection.sendPacket(new PacketPlayOutPosition(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch(), Set.of(), 0));
            CraftChunk chunk = (CraftChunk) craftPlayer.getChunk();
            entityPlayer.playerConnection.sendPacket(new PacketPlayOutViewCentre(chunk.getX(), chunk.getZ()));
            entityPlayer.playerConnection.sendPacket(new PacketPlayOutMapChunk(chunk.getHandle(), 65535, true));

            for (int x = chunk.getX() - Bukkit.getViewDistance(); x < chunk.getX() + Bukkit.getViewDistance(); x++)
                for (int z = chunk.getZ() - Bukkit.getViewDistance(); z < chunk.getZ() + Bukkit.getViewDistance(); z++) {
                    chunk.getWorld().getChunkAtAsync(x, z, false).whenComplete((foundChunk, throwable) -> {
                        CraftChunk craftChunk = (CraftChunk) foundChunk;
                        craftPlayer.getWorld().refreshChunk(craftChunk.getX(), craftChunk.getZ());
                        entityPlayer.playerConnection.sendPacket(new PacketPlayOutMapChunk(craftChunk.getHandle(), 65535, true));


                        for (Entity entity : craftChunk.getEntities()) {
                            CraftEntity craftEntity = (CraftEntity) entity;
                            if (entity instanceof LivingEntity) {
                                EntityLiving entityLiving = (EntityLiving) craftEntity.getHandle();
                                // EntityLiving Packet
                                entityPlayer.playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving((EntityLiving) craftEntity.getHandle()));
                                // Entity Effects Packets
                                entityLiving.getEffects().iterator().forEachRemaining(mobEffect -> entityPlayer.playerConnection.sendPacket(new PacketPlayOutEntityEffect(craftEntity.getEntityId(), mobEffect)));
                                List<Pair<EnumItemSlot, ItemStack>> contents = new ArrayList<>();

                                for (EnumItemSlot value : EnumItemSlot.values()) {
                                    ItemStack itemStack = entityLiving.getEquipment(value);
                                    if (itemStack != null)
                                        contents.add(new Pair<>(value, itemStack));
                                }
                                entityLiving.getId();
                                // Equipment Packet
                                entityPlayer.playerConnection.sendPacket(new PacketPlayOutEntityEquipment(entityLiving.getId(), contents));
                            } else if (entity instanceof Painting) {
                                entityPlayer.playerConnection.sendPacket(new PacketPlayOutSpawnEntityPainting((EntityPainting) craftEntity.getHandle()));
                            } else if (entity instanceof ExperienceOrb) {
                                entityPlayer.playerConnection.sendPacket(new PacketPlayOutSpawnEntityExperienceOrb((EntityExperienceOrb) craftEntity.getHandle()));
                            } else
                                // Packet for non Living Entities
                                entityPlayer.playerConnection.sendPacket(new PacketPlayOutSpawnEntity(craftEntity.getHandle()));

                            if (entity instanceof ItemFrame) {
                                CraftItemFrame craftItemFrame = (CraftItemFrame) entity;
                                CraftEntity passenger = (CraftEntity) craftItemFrame.getPassenger();
                                if (passenger != null)
                                    entityPlayer.playerConnection.sendPacket(new PacketPlayOutAttachEntity(passenger.getHandle(), craftItemFrame.getHandle()));
                            }
                            // Packet for Entity MetaData
                            entityPlayer.playerConnection.sendPacket(new PacketPlayOutEntityMetadata(craftEntity.getHandle().getId(), craftEntity.getHandle().getDataWatcher(), true));
                        }
                    });
                }
            dedicatedServer.getPlayerList().updateClient(entityPlayer);
        });
    }

    @Override
    public void sendFakeWorldBorder(@NotNull Player player, @NotNull Location center, @NonNegative double size, Runnable callback) {
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
    public void refreshWorldBorder(@NotNull Player player, Runnable callback) {
        VCorePaper.getInstance()
                .createTaskBatch()
                .doAsync(() -> {
                    CraftPlayer craftPlayer = (CraftPlayer) player;
                    PacketPlayOutWorldBorder packetPlayOutWorldBorder = new PacketPlayOutWorldBorder(craftPlayer.getHandle().getWorld().getWorldBorder(), PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE);
                    craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutWorldBorder);
                }).executeBatch(callback);
    }
}
