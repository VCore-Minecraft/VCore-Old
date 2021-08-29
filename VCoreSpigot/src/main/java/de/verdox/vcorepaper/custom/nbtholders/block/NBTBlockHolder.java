/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbtholders.block;

import de.tr7zw.changeme.nbtapi.NBTBlock;
import de.tr7zw.changeme.nbtapi.NBTChunk;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.verdox.vcorepaper.custom.nbtholders.NBTHolderImpl;
import org.bukkit.block.Block;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 23.08.2021 15:00
 */
public class NBTBlockHolder extends NBTHolderImpl<Block, NBTCompound> {
    private final NBTBlock nbtBlock;

    public NBTBlockHolder(Block dataHolder) {
        super(dataHolder);
        this.nbtBlock = new NBTBlock(dataHolder);
    }

    public boolean isNBTBlock() {
        NBTChunk nbtChunk = new NBTChunk(dataHolder.getChunk());
        if (!nbtChunk.getPersistentDataContainer().hasKey("blocks"))
            return false;
        return nbtChunk.getPersistentDataContainer().getCompound("blocks").hasKey(dataHolder.getX() + "_" + dataHolder.getY() + "_" + dataHolder.getZ());
    }

    @Override
    public NBTCompound getPersistentDataContainer() {
        return nbtBlock.getData();
    }

    @Override
    public NBTCompound getVanillaCompound() {
        return nbtBlock.getData();
    }
}
