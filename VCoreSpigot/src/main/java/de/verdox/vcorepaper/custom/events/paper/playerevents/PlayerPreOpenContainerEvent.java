/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.events.paper.playerevents;

import de.verdox.vcorepaper.custom.events.VCoreEvent;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 19.08.2021 21:44
 */
public class PlayerPreOpenContainerEvent extends VCoreEvent implements Cancellable {

    private final Player player;
    private final Container container;
    private boolean cancelled;

    /**
     * Called right before player attempts to open a container (Not called when trying to open an Ender Chest)
     *
     * @param player    The player
     * @param container The Container
     */
    public PlayerPreOpenContainerEvent(@NotNull Player player, @NotNull Container container) {
        this.player = player;
        this.container = container;
    }

    public Player getPlayer() {
        return player;
    }

    public Container getContainer() {
        return container;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
