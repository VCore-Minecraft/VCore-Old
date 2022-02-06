/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nms.impl.v1_17_1.impl.entity;

import de.verdox.vcore.nms.api.entity.NMSEntityHandler;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftVillager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 20.09.2021 20:04
 */
public class NMSEntityHandlerImpl implements NMSEntityHandler {
    @Override
    public int sendFakeNonLivingEntity(EntityType entityType, Location location, List<Player> visibleTo) {
        return 0;
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

    }

    @Override
    public void sendFakeItem(ItemStack itemStack, Location location, List<Player> visibleTo) {

    }

    @Override
    public void openTradingGUI(@NotNull Villager villager, @NotNull Player player) {
        CraftVillager craftVillager = (CraftVillager) villager;
        EntityVillager entityVillager = craftVillager.getHandle();
        EntityHuman entityHuman = ((CraftPlayer) player).getHandle();
        entityVillager.b(entityHuman, EnumHand.a);
    }

    @Override
    public int getOffers(@NotNull Villager villager) {
        CraftVillager craftVillager = (CraftVillager) villager;
        EntityVillager entityVillager = craftVillager.getHandle();
        return entityVillager.getOffers().size();
    }
}
