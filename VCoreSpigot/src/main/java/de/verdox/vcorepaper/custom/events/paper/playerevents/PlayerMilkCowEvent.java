/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.events.paper.playerevents;

import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.08.2021 01:06
 */
public class PlayerMilkCowEvent extends PlayerInteractEntityEvent {

    private final Cow cow;
    private boolean cancelled;

    public PlayerMilkCowEvent(@NotNull Player who, @NotNull Entity clickedEntity, @NotNull final EquipmentSlot hand) {
        super(who, clickedEntity);
        this.cow = (Cow) clickedEntity;
    }


    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * @return The Equipment Slot the player has used to milk the cow
     */

    public Cow getCow() {
        return cow;
    }
}
