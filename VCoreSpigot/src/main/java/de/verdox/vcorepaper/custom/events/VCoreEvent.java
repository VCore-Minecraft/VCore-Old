/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class VCoreEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public VCoreEvent() {
        super(false);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
