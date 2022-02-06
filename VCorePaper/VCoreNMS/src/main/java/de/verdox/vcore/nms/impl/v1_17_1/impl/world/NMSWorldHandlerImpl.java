/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nms.impl.v1_17_1.impl.world;

import com.mojang.datafixers.util.Pair;
import de.verdox.vcore.nms.api.reflection.java.FieldReflection;
import de.verdox.vcore.nms.api.world.NMSWorldHandler;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.util.VCoreUtil;
import de.verdox.vcorepaper.impl.plugin.VCorePaperPlugin;
import de.verdox.vcorepaper.utils.BukkitPlayerUtil;
import de.verdox.vcorepaper.utils.VanillaUtil;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityExperienceOrb;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.decoration.EntityPainting;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.dimension.DimensionManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_17_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftItemFrame;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 15.09.2021 21:30
 */
public class NMSWorldHandlerImpl implements NMSWorldHandler {

    private VCorePaperPlugin plugin;

    public NMSWorldHandlerImpl(VCorePaperPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void resetView(Player player, Runnable callback) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        CraftServer craftServer = (CraftServer) craftPlayer.getServer();
        DedicatedServer dedicatedServer = craftServer.getServer();
        dedicatedServer.getPlayerList().moveToWorld(craftPlayer.getHandle(), false);
    }

    @Override
    public void refreshChunks(Player player, Runnable callback) {
        plugin.createTaskBatch().doAsync(() -> {
            BukkitPlayerUtil.getChunksInServerViewDistance(player).forEach(chunkKey -> {
                World world = player.getWorld();
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
        PacketPlayOutMapChunk packetPlayOutMapChunk = new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle(), false);
        ((CraftPlayer) player).getHandle().networkManager.sendPacket(packetPlayOutMapChunk);
    }

    @Override
    public void sendFakeBiome(@NotNull Player player, @NotNull Chunk chunk, @NotNull Biome biome, Runnable callback) {
        CraftChunk craftChunk = (CraftChunk) chunk;
        CraftPlayer craftPlayer = (CraftPlayer) player;
        PacketPlayOutMapChunk packetPlayOutMapChunk = new PacketPlayOutMapChunk(craftChunk.getHandle(), true);

        try {
            Field biomesField = packetPlayOutMapChunk.getClass().getDeclaredField("f");
            biomesField.setAccessible(true);
            int[] biomeArray = (int[]) biomesField.get(packetPlayOutMapChunk);
            Arrays.fill(biomeArray, VanillaUtil.getBiomeID_1_17(biome));
            biomesField.set(packetPlayOutMapChunk, biomeArray);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        craftPlayer.getHandle().networkManager.sendPacket(packetPlayOutMapChunk);
        plugin.consoleMessage("&7Sent Fake Biome&7: &e" + biome + " &8[&6" + chunk.getX() + "&8|&6" + chunk.getZ() + "&8]", true);
    }

    @Override
    public void sendFakeDimension(@NotNull Player player, World.@NotNull Environment environment, Runnable callback) {
        Location location = player.getLocation().clone();
        boolean flag = !location.getWorld().getEnvironment().equals(environment);
        CraftPlayer craftPlayer = (CraftPlayer) player;
        CraftServer craftServer = (CraftServer) craftPlayer.getServer();
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        DedicatedServer dedicatedServer = craftServer.getServer();

        long seed = craftPlayer.getWorld().getSeed();

        ResourceKey<net.minecraft.world.level.World> world;
        DimensionManager dimensionManager;
        switch (environment) {
            case NORMAL: {
                world = net.minecraft.world.level.World.f;
                dimensionManager = FieldReflection.getField(DimensionManager.class, "n", DimensionManager.class).readField();
                break;
            }
            case NETHER: {
                world = net.minecraft.world.level.World.g;
                dimensionManager = FieldReflection.getField(DimensionManager.class, "o", DimensionManager.class).readField();
                break;
            }
            case THE_END: {
                world = net.minecraft.world.level.World.h;
                dimensionManager = FieldReflection.getField(DimensionManager.class, "p", DimensionManager.class).readField();
                break;
            }
            default:
                throw new IllegalStateException("Unknwon Environment: " + environment);
        }

        EnumGamemode enumGamemode = EnumGamemode.getById(player.getGameMode().getValue());
        craftPlayer.getHandle().networkManager.sendPacket(new PacketPlayOutRespawn(dimensionManager, world, seed, enumGamemode, enumGamemode, false, false, flag));
        entityPlayer.networkManager.sendPacket(new PacketPlayOutPosition(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch(), Set.of(), 0, false));
        CraftChunk chunk = (CraftChunk) craftPlayer.getChunk();
        entityPlayer.networkManager.sendPacket(new PacketPlayOutViewCentre(chunk.getX(), chunk.getZ()));
        entityPlayer.networkManager.sendPacket(new PacketPlayOutMapChunk(chunk.getHandle(), true));

        for (int x = chunk.getX() - Bukkit.getViewDistance(); x < chunk.getX() + Bukkit.getViewDistance(); x++)
            for (int z = chunk.getZ() - Bukkit.getViewDistance(); z < chunk.getZ() + Bukkit.getViewDistance(); z++) {
                chunk.getWorld().getChunkAtAsync(x, z, false).whenComplete((foundChunk, throwable) -> {
                    CraftChunk craftChunk = (CraftChunk) foundChunk;
                    craftPlayer.getWorld().refreshChunk(craftChunk.getX(), craftChunk.getZ());
                    entityPlayer.networkManager.sendPacket(new PacketPlayOutMapChunk(craftChunk.getHandle(), true));


                    for (Entity entity : craftChunk.getEntities()) {
                        CraftEntity craftEntity = (CraftEntity) entity;
                        if (entity instanceof LivingEntity) {
                            EntityLiving entityLiving = (EntityLiving) craftEntity.getHandle();
                            // Packet for Player
                            if (entity instanceof Player && !entity.equals(player)) {
                                entityPlayer.networkManager.sendPacket(new PacketPlayOutNamedEntitySpawn((EntityHuman) craftEntity.getHandle()));
                            } else
                                // EntityLiving Packet
                                entityPlayer.networkManager.sendPacket(new PacketPlayOutSpawnEntityLiving((EntityLiving) craftEntity.getHandle()));
                            // Entity Effects Packets
                            entityLiving.getEffects().iterator().forEachRemaining(mobEffect -> entityPlayer.networkManager.sendPacket(new PacketPlayOutEntityEffect(craftEntity.getEntityId(), mobEffect)));
                            List<Pair<EnumItemSlot, ItemStack>> contents = new ArrayList<>();

                            for (EnumItemSlot value : EnumItemSlot.values()) {
                                ItemStack itemStack = entityLiving.getEquipment(value);
                                if (itemStack != null)
                                    contents.add(new Pair<>(value, itemStack));
                            }
                            entityLiving.getId();
                            // Equipment Packet
                            entityPlayer.networkManager.sendPacket(new PacketPlayOutEntityEquipment(entityLiving.getId(), contents));
                        } else if (entity instanceof Painting) {
                            entityPlayer.networkManager.sendPacket(new PacketPlayOutSpawnEntityPainting((EntityPainting) craftEntity.getHandle()));
                        } else if (entity instanceof ExperienceOrb) {
                            entityPlayer.networkManager.sendPacket(new PacketPlayOutSpawnEntityExperienceOrb((EntityExperienceOrb) craftEntity.getHandle()));
                        } else
                            // Packet for non Living Entities
                            entityPlayer.networkManager.sendPacket(new PacketPlayOutSpawnEntity(craftEntity.getHandle()));

                        if (entity instanceof ItemFrame) {
                            CraftItemFrame craftItemFrame = (CraftItemFrame) entity;
                            CraftEntity passenger = (CraftEntity) craftItemFrame.getPassenger();
                            if (passenger != null)
                                entityPlayer.networkManager.sendPacket(new PacketPlayOutAttachEntity(passenger.getHandle(), craftItemFrame.getHandle()));
                        }
                        // Packet for Entity MetaData
                        entityPlayer.networkManager.sendPacket(new PacketPlayOutEntityMetadata(craftEntity.getHandle().getId(), craftEntity.getHandle().getDataWatcher(), true));
                    }
                });
            }
        dedicatedServer.getPlayerList().updateClient(entityPlayer);
        if (callback != null)
            callback.run();
        //VCorePaper.getInstance().consoleMessage("&7Sent Fake Dimension&7: &e" + environment, true);
    }

    @Override
    public void sendFakeWorldBorder(@NotNull Player player, @NotNull Location center, @NonNegative double size, Runnable callback) {

    }

    @Override
    public void refreshWorldBorder(@NotNull Player player, Runnable callback) {

    }
}
