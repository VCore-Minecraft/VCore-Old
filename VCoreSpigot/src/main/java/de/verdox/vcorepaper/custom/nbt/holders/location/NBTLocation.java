/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbt.holders.location;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTFile;
import de.verdox.vcore.plugin.wrapper.types.WorldChunk;
import de.verdox.vcore.util.bukkit.keys.ChunkKey;
import de.verdox.vcore.util.bukkit.keys.LocationKey;
import de.verdox.vcore.util.bukkit.keys.SplitChunkKey;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.nbt.block.VBlock;
import de.verdox.vcorepaper.custom.nbt.holders.NBTHolderImpl;
import de.verdox.vcorepaper.custom.nbt.holders.location.event.nbtlocation.VBlockDeleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.IOException;
import java.util.Objects;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.08.2021 13:33
 */
public class NBTLocation extends NBTHolderImpl<Location, NBTCompound> {

    private final int chunkY;
    private final int chunkX;
    private final int chunkZ;
    private final WorldChunk worldChunk;
    private final SplitChunkKey splitChunkKey;

    private NBTFile nbtFile;
    private NBTCompound chunkCompound;
    private NBTCompound splitChunkCompound;
    private NBTCompound blockCompound;
    private final VBlock.LocationBased vBlock;

    public NBTLocation(VBlock.LocationBased vBlock, Location location) {
        super(location);
        this.vBlock = vBlock;
        chunkY = dataHolder.getBlockY() / 16;
        chunkX = dataHolder.getBlockX() >> 4;
        chunkZ = dataHolder.getBlockZ() >> 4;

        worldChunk = new WorldChunk(dataHolder.getWorld().getName(), chunkX, chunkZ);
        splitChunkKey = new SplitChunkKey(worldChunk, chunkY);
    }


    protected NBTCompound getOrLoadLocationCompound() {
        if (Objects.isNull(this.nbtFile)) {
            this.nbtFile = VCorePaper.getInstance().getBlockFileStorage().getWorldStorage(worldChunk.worldName).getNBTFile(WorldChunk.getRegionX(worldChunk.x), WorldChunk.getRegionZ(worldChunk.z));
            if (this.nbtFile == null) {
                throw new IllegalStateException("Could not load NBT Location");
            }
        }
        if (Objects.isNull(this.chunkCompound))
            this.chunkCompound = this.nbtFile.getOrCreateCompound(((ChunkKey) splitChunkKey).toString());
        if (Objects.isNull(this.splitChunkCompound))
            this.splitChunkCompound = this.chunkCompound.getOrCreateCompound(splitChunkKey.toString());
        if (Objects.isNull(this.blockCompound))
            this.blockCompound = this.splitChunkCompound.getOrCreateCompound(new LocationKey(this.dataHolder).toStringWithoutWorld());
        return this.blockCompound;
    }

    public boolean isNBTLocation() {
        this.nbtFile = VCorePaper.getInstance().getBlockFileStorage().getWorldStorage(worldChunk.worldName).getNBTFile(WorldChunk.getRegionX(worldChunk.x), WorldChunk.getRegionZ(worldChunk.z));
        if (this.nbtFile == null || !this.nbtFile.hasKey(((ChunkKey) splitChunkKey).toString()))
            return false;
        this.chunkCompound = this.nbtFile.getCompound(((ChunkKey) splitChunkKey).toString());
        if (!this.chunkCompound.hasKey(splitChunkKey.toString()))
            return false;
        this.splitChunkCompound = this.chunkCompound.getCompound(splitChunkKey.toString());
        if (!this.splitChunkCompound.hasKey(new LocationKey(this.dataHolder).toStringWithoutWorld()))
            return false;
        this.blockCompound = this.splitChunkCompound.getCompound(new LocationKey(this.dataHolder).toStringWithoutWorld());
        return !this.blockCompound.getKeys().isEmpty();
    }

    /**
     * Not usable right now -> Object references must be declared new if compounds are cleared
     */
    private void clearGarbage() {
        if (this.blockCompound != null && blockCompound.getKeys().isEmpty()) {
            splitChunkCompound.removeKey(blockCompound.getName());
            this.blockCompound = null;
        }
        if (this.splitChunkCompound != null && splitChunkCompound.getKeys().isEmpty()) {
            chunkCompound.removeKey(splitChunkCompound.getName());
            this.splitChunkCompound = null;
        }
        if (this.chunkCompound != null && chunkCompound.getKeys().isEmpty())
            nbtFile.removeKey(chunkCompound.getName());
    }

    @Override
    public void save() {
        try {
            clearGarbage();
            nbtFile.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Returns the NBT Storage of a Tile Entity if the BlockState of the block at the specified location is instance of TileState
     * Note that in order to make this function work it is probably needed for the chunk to be loaded where the block is located at
     * <p>
     * This function is not thread safe in every server software (e.g. Spigot)
     *
     * @return Vanilla Compound of the block if exists
     */
    @Override
    public NBTCompound getVanillaCompound() {
        return getOrLoadLocationCompound();
    }

    @Override
    public NBTCompound getPersistentDataContainer() {
        return getOrLoadLocationCompound();
    }

    @Override
    public void delete() {
        VBlockDeleteEvent NBTBlockDeleteEvent = new VBlockDeleteEvent(vBlock);
        Bukkit.getPluginManager().callEvent(NBTBlockDeleteEvent);
        if (!NBTBlockDeleteEvent.isCancelled()) {
            splitChunkCompound.removeKey(new LocationKey(this.dataHolder).toStringWithoutWorld());
            this.blockCompound = null;
            save();
        }
    }
}
