/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbtholders.block;

import de.tr7zw.changeme.nbtapi.NBTBlock;
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

    @Override
    public NBTCompound getPersistentDataContainer() {
        return nbtBlock.getData();
    }

    @Override
    public NBTCompound getVanillaCompound() {
        return nbtBlock.getData();
    }
}
