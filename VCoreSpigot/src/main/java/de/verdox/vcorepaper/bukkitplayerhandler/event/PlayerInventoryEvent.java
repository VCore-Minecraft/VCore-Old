/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.bukkitplayerhandler.event;

import de.verdox.vcorepaper.custom.events.VCoreHybridEvent;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 12.07.2021 20:29
 */
public abstract class PlayerInventoryEvent extends VCoreHybridEvent {
    private Player player;
    private String inventoryToRestore;

    public PlayerInventoryEvent(@Nonnull Player player, @Nonnull String inventoryToRestore){
        this.player = player;
        this.inventoryToRestore = inventoryToRestore;
    }

    public Player getPlayer() {
        return player;
    }

    public String getInventoryID() {
        return inventoryToRestore;
    }

    public void setInventoryID(@Nonnull String inventoryToRestore) {
        this.inventoryToRestore = inventoryToRestore;
    }

    public void setPlayer(@Nonnull Player player) {
        this.player = player;
    }
}
