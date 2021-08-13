/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbtholders.block;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTFile;
import de.verdox.vcore.plugin.wrapper.types.WorldChunk;
import de.verdox.vcore.util.bukkit.keys.ChunkKey;
import de.verdox.vcore.util.bukkit.keys.LocationKey;
import de.verdox.vcore.util.bukkit.keys.SplitChunkKey;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.nbtholders.NBTHolder;
import de.verdox.vcorepaper.custom.nbtholders.block.event.NBTBlockDeleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.08.2021 13:33
 */
public class NBTBlock implements NBTHolder {

    private final Location location;
    private final NBTFile nbtFile;
    private final NBTCompound chunkCompound;
    private final NBTCompound splitChunkCompound;
    private final NBTCompound blockCompound;

    public NBTBlock(Location location){
        this.location = location;

        int chunkY = location.getBlockY() / 16;
        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;

        WorldChunk worldChunk = new WorldChunk(location.getWorld().getName(),chunkX,chunkZ);
        SplitChunkKey splitChunkKey = new SplitChunkKey(worldChunk,chunkY);

        try {
            this.nbtFile = VCorePaper.getInstance().getBlockFileStorage().loadNBTFile(worldChunk).get(10,TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            VCorePaper.getInstance().consoleMessage("&cCould not load NBT File for: "+location,true);
            e.printStackTrace();
            throw new IllegalStateException();
        }

        this.chunkCompound = this.nbtFile.getOrCreateCompound(((ChunkKey) splitChunkKey).toString());
        this.splitChunkCompound = this.chunkCompound.getOrCreateCompound(splitChunkKey.toString());
        this.blockCompound = this.splitChunkCompound.getOrCreateCompound(new LocationKey(this.location).toStringWithoutWorld());
    }

    @Override
    public Boolean getBoolean(String key) {
        return blockCompound.getBoolean(key);
    }

    @Override
    public Integer getInteger(String key) {
        return blockCompound.getInteger(key);
    }

    @Override
    public Double getDouble(String key) {
        return blockCompound.getDouble(key);
    }

    @Override
    public String getString(String key) {
        return blockCompound.getString(key);
    }

    @Override
    public UUID getUUID(String key) {
        return blockCompound.getUUID(key);
    }

    @Override
    public Long getLong(String key) {
        return blockCompound.getLong(key);
    }

    @Override
    public ItemStack getItemStack(String key) {
        return blockCompound.getItemStack(key);
    }

    @Override
    public <T> T getObject(String key, Class<T> type) {
        return blockCompound.getObject(key,type);
    }

    @Override
    public void setObject(String key, Object value) {
        blockCompound.setObject(key,value);
    }

    @Override
    public Set<String> getKeys() {
        return blockCompound.getKeys();
    }

    @Override
    public boolean hasKey(String key) {
        return blockCompound.hasKey(key);
    }

    @Override
    public void removeKey(String key) {
        blockCompound.removeKey(key);
    }

    @Override
    public void save() {
        try {
            String key = new LocationKey(this.location).toStringWithoutWorld();
            if(splitChunkCompound.hasKey(key))
                if(splitChunkCompound.getCompound(key).getKeys().isEmpty())
                    splitChunkCompound.removeKey(key);
            nbtFile.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete(){
        NBTBlockDeleteEvent NBTBlockDeleteEvent = new NBTBlockDeleteEvent(this);
        Bukkit.getPluginManager().callEvent(NBTBlockDeleteEvent);
        if(!NBTBlockDeleteEvent.isCancelled())
            splitChunkCompound.removeKey(new LocationKey(this.location).toStringWithoutWorld());
    }

    public Location getLocation() {
        return location;
    }
}
