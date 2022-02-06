/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nms.api.entity;

import de.verdox.vcore.nms.NMSHandler;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.06.2021 14:48
 */
public interface NMSEntityHandler extends NMSHandler {

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

    void openTradingGUI(@NotNull Villager villager, @NotNull Player player);

    int getOffers(@NotNull Villager villager);
}
