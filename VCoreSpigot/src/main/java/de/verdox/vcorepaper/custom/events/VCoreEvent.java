package de.verdox.vcorepaper.custom.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class VCoreEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public VCoreEvent(){
        super(false);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
