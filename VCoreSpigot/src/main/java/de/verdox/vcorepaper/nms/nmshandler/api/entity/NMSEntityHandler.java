/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms.nmshandler.api.entity;

import de.verdox.vcorepaper.nms.NMSHandler;
import de.verdox.vcorepaper.nms.NMSVersion;
import de.verdox.vcorepaper.nms.nmshandler.v1_16_3.entity.EntityHandler_V1_16_R3;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.06.2021 14:48
 */
public interface NMSEntityHandler extends NMSHandler {

    static NMSEntityHandler getRightHandler(NMSVersion nmsVersion) {
        if (nmsVersion.equals(NMSVersion.V1_16_5)) {
            return new EntityHandler_V1_16_R3();
        }
        throw new NotImplementedException("This Handler [" + NMSEntityHandler.class.getName() + "] is not implemented for NMS version: " + nmsVersion.getNmsVersionTag());
    }

    /**
     * Send a Fake Entity to a player
     *
     * @param entityType EntityType to Spawn
     * @param location   Location where to spawn
     * @param visibleTo  Players who see the entity
     * @return entityID of fake Entity
     */
    int sendFakeNonLivingEntity(EntityType entityType, Location location, List<Player> visibleTo);

    /**
     * Send a Fake Entity to a player
     *
     * @param entityType EntityType to Spawn
     * @param location   Location where to spawn
     * @param visibleTo  Players who see the entity
     * @return entityID of fake Entity
     */
    int sendFakeLivingEntity(EntityType entityType, Location location, List<Player> visibleTo);

    void sendFakeEntityMovement(EntityType entityType, Location location, List<Player> visibleTo);

    void sendFakeEntityTeleport(EntityType entityType, Location location, List<Player> visibleTo);

    void sendArmorStandWithName(String name, Location location, List<Player> visibleTo);

    void sendFakeItem(ItemStack itemStack, Location location, List<Player> visibleTo);
}
