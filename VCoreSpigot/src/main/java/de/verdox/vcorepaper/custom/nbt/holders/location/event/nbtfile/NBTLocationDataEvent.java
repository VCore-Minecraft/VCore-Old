/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbt.holders.location.event.nbtfile;

import de.verdox.vcore.plugin.wrapper.types.WorldRegion;
import de.verdox.vcorepaper.custom.events.VCoreHybridEvent;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 23.08.2021 14:36
 */
public abstract class NBTLocationDataEvent extends VCoreHybridEvent implements Cancellable {

    private final WorldRegion worldRegion;
    private final Location location;

    private boolean cancelled;

    public NBTLocationDataEvent(WorldRegion worldRegion, Location location) {
        this.worldRegion = worldRegion;
        this.location = location;
    }

    @NotNull
    public WorldRegion getWorldRegion() {
        return worldRegion;
    }

    @NotNull
    public Location getLocation() {
        return location;
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
