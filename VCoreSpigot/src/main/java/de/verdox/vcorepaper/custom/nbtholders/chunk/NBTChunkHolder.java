/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbtholders.chunk;

import de.tr7zw.changeme.nbtapi.NBTChunk;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.verdox.vcorepaper.custom.nbtholders.NBTHolderImpl;
import org.bukkit.Chunk;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 23.08.2021 15:02
 */
public class NBTChunkHolder extends NBTHolderImpl<Chunk, NBTCompound> {
    private final NBTChunk nbtChunk;

    public NBTChunkHolder(Chunk dataHolder) {
        super(dataHolder);
        this.nbtChunk = new NBTChunk(dataHolder);
    }

    @Override
    public NBTCompound getPersistentDataContainer() {
        return nbtChunk.getPersistentDataContainer();
    }

    @Override
    public NBTCompound getVanillaCompound() {
        return nbtChunk.getPersistentDataContainer();
    }
}
