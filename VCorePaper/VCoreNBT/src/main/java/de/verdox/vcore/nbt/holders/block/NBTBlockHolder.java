/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nbt.holders.block;

import de.tr7zw.changeme.nbtapi.NBTBlock;
import de.tr7zw.changeme.nbtapi.NBTChunk;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.verdox.vcore.nbt.block.VBlock;
import de.verdox.vcore.nbt.holders.NBTHolderImpl;
import de.verdox.vcore.nbt.holders.location.event.nbtlocation.VBlockDeleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 23.08.2021 15:00
 */
public class NBTBlockHolder extends NBTHolderImpl<Block, NBTCompound> {
    private final VBlock.BlockBased vBlock;
    private final NBTChunk nbtChunk;

    private NBTBlock nbtBlock;

    public NBTBlockHolder(VBlock.BlockBased vBlock, Block dataHolder) {
        super(dataHolder);
        this.vBlock = vBlock;
        nbtChunk = new NBTChunk(dataHolder.getChunk());
    }

    public boolean isNBTBlock() {
        if (!nbtChunk.getPersistentDataContainer().hasKey("blocks"))
            return false;
        return nbtChunk.getPersistentDataContainer().getCompound("blocks").hasKey(key());
    }

    public VBlock.BlockBased getVBlock() {
        return vBlock;
    }

    @Override
    public NBTCompound getPersistentDataContainer() {
        if (this.nbtBlock == null)
            nbtBlock = new NBTBlock(dataHolder);
        return nbtBlock.getData();
    }

    @Override
    public void delete() {
        if (!isNBTBlock())
            return;
        VBlockDeleteEvent vBlockDeleteEvent = new VBlockDeleteEvent(vBlock);
        Bukkit.getPluginManager().callEvent(vBlockDeleteEvent);
        if (!vBlockDeleteEvent.isCancelled())
            nbtChunk.getPersistentDataContainer().getOrCreateCompound("blocks").removeKey(key());
    }

    @Override
    public NBTCompound getVanillaCompound() {
        return getPersistentDataContainer();
    }

    private String key() {
        return dataHolder.getX() + "_" + dataHolder.getY() + "_" + dataHolder.getZ();
    }
}
