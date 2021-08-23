/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbtholders.chunk;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.verdox.vcorepaper.custom.nbtholders.NBTHolder;
import de.verdox.vcorepaper.custom.nbtholders.NBTHolderImpl;
import org.bukkit.Chunk;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 23.08.2021 15:02
 */
public class NBTChunk extends NBTHolderImpl<Chunk> {
    public NBTChunk(Chunk dataHolder) {
        super(dataHolder);
    }

    @Override
    public NBTHolder getVanillaCompound() {
        return this;
    }

    @Override
    protected NBTCompound getNbtCompound() {
        return new de.tr7zw.changeme.nbtapi.NBTChunk(dataHolder).getPersistentDataContainer();
    }
}
