/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.events;


import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 13.07.2021 00:31
 */
public abstract class VCoreHybridEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public VCoreHybridEvent() {
        super(!Bukkit.isPrimaryThread());
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
