/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbtholders;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 23.08.2021 13:57
 */
public abstract class NBTHolderImpl<T> implements NBTHolder {

    protected final T dataHolder;

    public NBTHolderImpl(T dataHolder) {
        this.dataHolder = dataHolder;
    }

    protected abstract NBTCompound getNbtCompound();

    @Override
    public ItemStack getItemStack(String key) {
        return getNbtCompound().getItemStack(key);
    }

    @Override
    public <T> T getObject(String key, Class<T> type) {
        return getNbtCompound().getObject(key, type);
    }

    @Override
    public UUID getUUID(String key) {
        return getNbtCompound().getUUID(key);
    }

    @Override
    public Long getLong(String key) {
        return getNbtCompound().getLong(key);
    }

    @Override
    public String getString(String key) {
        return getNbtCompound().getString(key);
    }

    @Override
    public Double getDouble(String key) {
        return getNbtCompound().getDouble(key);
    }

    @Override
    public Integer getInteger(String key) {
        return getNbtCompound().getInteger(key);
    }

    @Override
    public Boolean getBoolean(String key) {
        return getNbtCompound().getBoolean(key);
    }

    @Override
    public Byte getByte(String key) {
        return getNbtCompound().getByte(key);
    }

    @Override
    public byte[] getByteArray(String key) {
        return getNbtCompound().getByteArray(key);
    }

    @Override
    public Float getFloat(String key) {
        return getNbtCompound().getFloat(key);
    }

    @Override
    public void setObject(String key, Object value) {
        getNbtCompound().setObject(key, value);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        getNbtCompound().setBoolean(key, value);
    }

    @Override
    public void setInteger(String key, int value) {
        getNbtCompound().setInteger(key, value);
    }

    @Override
    public void setByte(String key, byte value) {
        getNbtCompound().setByte(key, value);
    }

    @Override
    public void setDouble(String key, double value) {
        getNbtCompound().setDouble(key, value);
    }

    @Override
    public void setByteArray(String key, byte[] value) {
        getNbtCompound().setByteArray(key, value);
    }

    @Override
    public void setFloat(String key, float value) {
        getNbtCompound().setFloat(key, value);
    }

    @Override
    public void setIntArray(String key, int[] value) {
        getNbtCompound().setIntArray(key, value);
    }

    @Override
    public void setItemStack(String key, ItemStack value) {
        getNbtCompound().setItemStack(key, value);
    }

    @Override
    public void setLong(String key, long value) {
        getNbtCompound().setLong(key, value);
    }

    @Override
    public void setShort(String key, short value) {
        getNbtCompound().setShort(key, value);
    }

    @Override
    public void setString(String key, String value) {
        getNbtCompound().setString(key, value);
    }

    @Override
    public void setUUID(String key, UUID value) {
        getNbtCompound().setUUID(key, value);
    }

    @Override
    public Set<String> getKeys() {
        return getNbtCompound().getKeys();
    }

    @Override
    public boolean hasKey(String key) {
        return getNbtCompound().hasKey(key);
    }

    @Override
    public void removeKey(String key) {
        getNbtCompound().removeKey(key);
    }

    @Override
    public void save() {

    }

    @Override
    public void delete() {
        for (String key : getNbtCompound().getKeys()) {
            getNbtCompound().removeKey(key);
        }
    }
}
