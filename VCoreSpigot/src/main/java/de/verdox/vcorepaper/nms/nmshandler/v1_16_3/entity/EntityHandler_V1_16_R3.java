/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms.nmshandler.v1_16_3.entity;

import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_16_R3.Vec3D;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import de.verdox.vcorepaper.nms.nmshandler.api.entity.NMSEntityHandler;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.06.2021 14:57
 */
public class EntityHandler_V1_16_R3 implements NMSEntityHandler {
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
        Vec3D vec3D = new Vec3D(0,0,0);

        EntityTypes<?> types = EntityTypes.a(entityType.name().toLowerCase(Locale.ROOT)).orElse(null);
        if(types == null)
            throw new NullPointerException("Could not find NMS EntityType: "+entityType);

        PacketPlayOutSpawnEntity packetPlayOutSpawnEntity = new PacketPlayOutSpawnEntity(entityID,uuid,xPos,yPos,zPos,pitch,yaw,types,data,vec3D);
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
}
