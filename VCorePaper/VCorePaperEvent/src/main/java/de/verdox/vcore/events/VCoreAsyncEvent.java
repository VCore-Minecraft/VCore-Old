/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class VCoreAsyncEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public VCoreAsyncEvent() {
        super(true);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
