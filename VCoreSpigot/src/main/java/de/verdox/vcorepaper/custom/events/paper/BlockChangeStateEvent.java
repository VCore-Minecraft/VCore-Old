/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.events.paper;

import de.verdox.vcorepaper.custom.events.VCoreEvent;
import org.bukkit.block.Block;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.08.2021 22:51
 */
public class BlockChangeStateEvent extends BlockEvent {
    private static final HandlerList handlers = new HandlerList();
    private final boolean materialChanged;

    private boolean cancelled;

    public BlockChangeStateEvent(@NotNull Block theBlock, boolean materialChanged) {
        super(theBlock);
        this.materialChanged = materialChanged;
    }

    public boolean isMaterialChanged() {
        return materialChanged;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
