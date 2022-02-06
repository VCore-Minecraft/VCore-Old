/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nms.impl.v1_16_3.impl.entity;

import de.verdox.vcore.nms.api.entity.NMSEntityHandler;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.06.2021 14:57
 */
public class NMSEntityHandlerImpl implements NMSEntityHandler {
    @Override
    public int sendFakeNonLivingEntity(EntityType entityType, Location location, List<Player> visibleTo) {

        Random random = new Random();
        int entityID = random.nextInt(5000) + (int) System.currentTimeMillis();
        UUID uuid = UUID.randomUUID();
        double xPos = location.getX();
        double yPos = location.getY();
        double zPos = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();
        // When data is greater 0 Vec3D is used as velocity vector
        int data = 0;
        Vec3D vec3D = new Vec3D(0, 1, 0);

        EntityTypes<?> types = EntityTypes.a(entityType.name().toLowerCase(Locale.ROOT)).orElse(null);
        if (types == null)
            throw new NullPointerException("Could not find NMS EntityType: " + entityType);

        PacketPlayOutSpawnEntity packetPlayOutSpawnEntity = new PacketPlayOutSpawnEntity(entityID, uuid, xPos, yPos, zPos, pitch, yaw, types, data, vec3D);
        visibleTo.forEach(player -> {
            CraftPlayer craftPlayer = (CraftPlayer) player;
            craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutSpawnEntity);
        });

        return entityID;
    }

    @Override
    public int sendFakeLivingEntity(EntityType entityType, Location location, List<Player> visibleTo) {
        return 0;
    }

    @Override
    public void sendFakeEntityMovement(EntityType entityType, Location location, List<Player> visibleTo) {

    }

    @Override
    public void sendFakeEntityTeleport(EntityType entityType, Location location, List<Player> visibleTo) {

    }

    @Override
    public void sendArmorStandWithName(String name, Location location, List<Player> visibleTo) {

        CraftWorld craftWorld = (CraftWorld) location.getWorld();
        WorldServer worldServer = craftWorld.getHandle();
        EntityArmorStand entityArmorStand = new EntityArmorStand(EntityTypes.ARMOR_STAND, worldServer);
        entityArmorStand.setInvisible(true);
        entityArmorStand.setSmall(true);
        entityArmorStand.setCustomNameVisible(true);
        entityArmorStand.setCustomName(new ChatComponentText(name));
        entityArmorStand.setLocation(location.getX(), location.getY(), location.getZ(), 0, 0);
        entityArmorStand.setSlot(EnumItemSlot.HEAD, ItemStack.fromBukkitCopy(new org.bukkit.inventory.ItemStack(Material.DIRT)));
        PacketPlayOutSpawnEntity packetPlayOutSpawnEntity = new PacketPlayOutSpawnEntity(entityArmorStand);
        PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(entityArmorStand.getId(), entityArmorStand.getDataWatcher(), true);

        visibleTo.forEach(player -> {
            CraftPlayer craftPlayer = (CraftPlayer) player;
            craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutSpawnEntity);
            craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutEntityMetadata);
        });


    }

    @Override
    public void sendFakeItem(org.bukkit.inventory.ItemStack itemStack, Location location, List<Player> visibleTo) {
        CraftWorld craftWorld = (CraftWorld) location.getWorld();
        WorldServer worldServer = craftWorld.getHandle();
        EntityItem entityItem = new EntityItem(worldServer, location.getX(), location.getY(), location.getZ(), ItemStack.fromBukkitCopy(itemStack));
        entityItem.setOwner(UUID.randomUUID());
        entityItem.setNoGravity(true);

        PacketPlayOutSpawnEntity packetPlayOutSpawnEntity = new PacketPlayOutSpawnEntity(entityItem);
        PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(entityItem.getId(), entityItem.getDataWatcher(), true);

        visibleTo.forEach(player -> {
            CraftPlayer craftPlayer = (CraftPlayer) player;
            craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutSpawnEntity);
            craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutEntityMetadata);
        });
    }

    @Override
    public void openTradingGUI(@NotNull Villager villager, @NotNull Player player) {

    }

    @Override
    public int getOffers(@NotNull Villager villager) {
        return 0;
    }
}