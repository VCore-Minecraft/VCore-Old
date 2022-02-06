/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nbt.holders.location.event.nbtfile;

import de.tr7zw.changeme.nbtapi.NBTBlock;
import de.verdox.vcore.plugin.wrapper.types.WorldRegion;
import org.bukkit.Location;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 23.08.2021 14:39
 */
public class NBTLocationDataUnloadEvent extends NBTLocationDataEvent {
    private final NBTBlock nbtBlock;

    public NBTLocationDataUnloadEvent(WorldRegion worldRegion, Location location, NBTBlock nbtBlock) {
        super(worldRegion, location);
        this.nbtBlock = nbtBlock;
    }

    public NBTBlock getNbtBlock() {
        return nbtBlock;
    }
}
