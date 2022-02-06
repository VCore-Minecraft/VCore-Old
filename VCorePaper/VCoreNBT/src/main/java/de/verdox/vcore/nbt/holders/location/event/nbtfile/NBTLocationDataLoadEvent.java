/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nbt.holders.location.event.nbtfile;

import de.verdox.vcore.plugin.wrapper.types.WorldRegion;
import org.bukkit.Location;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 23.08.2021 14:39
 */
public class NBTLocationDataLoadEvent extends NBTLocationDataEvent {
    public NBTLocationDataLoadEvent(WorldRegion worldRegion, Location location) {
        super(worldRegion, location);
    }
}
