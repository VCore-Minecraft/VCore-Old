/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbtholders.location.event.nbtfile;

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
