/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbtholders;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
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
    public ItemStack getItemStack(@NotNull String key) {
        return Objects.requireNonNull(getNbtCompound()).getItemStack(key);
    }

    @Override
    public <V> V getObject(@NotNull String key, @NotNull Class<V> type) {
        if (type.equals(UUID.class)) {
            return type.cast(getUUID(key));
        }
        if (type.equals(ItemStack.class))
            return type.cast(getItemStack(key));
        if (type.equals(Long.class) || getNbtCompound().getType(key).equals(NBTType.NBTTagLong))
            return type.cast(getLong(key));
        if (type.equals(String.class) || getNbtCompound().getType(key).equals(NBTType.NBTTagString))
            return type.cast(getString(key));
        if (type.equals(Double.class) || getNbtCompound().getType(key).equals(NBTType.NBTTagDouble))
            return type.cast(getDouble(key));
        if (type.equals(Integer.class) || getNbtCompound().getType(key).equals(NBTType.NBTTagInt))
            return type.cast(getInteger(key));
        if (type.equals(Boolean.class))
            return type.cast(getBoolean(key));
        if (type.equals(Byte.class) || getNbtCompound().getType(key).equals(NBTType.NBTTagByte))
            return type.cast(getByte(key));
        if (type.equals(Byte[].class) || getNbtCompound().getType(key).equals(NBTType.NBTTagByteArray))
            return type.cast(getByteArray(key));
        if (type.equals(Float.class) || getNbtCompound().getType(key).equals(NBTType.NBTTagFloat))
            return type.cast(getFloat(key));
        return Objects.requireNonNull(getNbtCompound()).getObject(key, type);
    }

    @Override
    public UUID getUUID(@NotNull String key) {
        return Objects.requireNonNull(getNbtCompound()).getUUID(key);
    }

    @Override
    public Long getLong(@NotNull String key) {
        return Objects.requireNonNull(getNbtCompound()).getLong(key);
    }

    @Override
    public String getString(@NotNull String key) {
        return Objects.requireNonNull(getNbtCompound()).getString(key);
    }

    @Override
    public Double getDouble(@NotNull String key) {
        return Objects.requireNonNull(getNbtCompound()).getDouble(key);
    }

    @Override
    public Integer getInteger(@NotNull String key) {
        return Objects.requireNonNull(getNbtCompound()).getInteger(key);
    }

    @Override
    public Boolean getBoolean(@NotNull String key) {
        return Objects.requireNonNull(getNbtCompound()).getBoolean(key);
    }

    @Override
    public Byte getByte(@NotNull String key) {
        return Objects.requireNonNull(getNbtCompound()).getByte(key);
    }

    @Override
    public byte[] getByteArray(@NotNull String key) {
        return Objects.requireNonNull(getNbtCompound()).getByteArray(key);
    }

    @Override
    public Float getFloat(@NotNull String key) {
        return Objects.requireNonNull(getNbtCompound()).getFloat(key);
    }

    @Override
    public void setObject(@NotNull String key, Object value) {
        if (value instanceof UUID)
            setUUID(key, (UUID) value);
        else if (value instanceof ItemStack)
            setItemStack(key, (ItemStack) value);
        else if (value instanceof Long)
            setLong(key, (Long) value);
        else if (value instanceof String)
            setString(key, (String) value);
        else if (value instanceof Double)
            setDouble(key, (Double) value);
        else if (value instanceof Integer)
            setInteger(key, (Integer) value);
        else if (value instanceof Boolean)
            setBoolean(key, (Boolean) value);
        else if (value instanceof Byte)
            setByte(key, (Byte) value);
        else if (value instanceof Byte[])
            setByteArray(key, (Byte[]) value);
        else if (value instanceof Float)
            setFloat(key, (Float) value);
        else
            Objects.requireNonNull(getNbtCompound()).setObject(key, value);
    }

    @Override
    public void setBoolean(@NotNull String key, boolean value) {
        Objects.requireNonNull(getNbtCompound()).setBoolean(key, value);
    }

    @Override
    public void setInteger(@NotNull String key, int value) {
        Objects.requireNonNull(getNbtCompound()).setInteger(key, value);
    }

    @Override
    public void setByte(@NotNull String key, byte value) {
        Objects.requireNonNull(getNbtCompound()).setByte(key, value);
    }

    @Override
    public void setDouble(@NotNull String key, double value) {
        Objects.requireNonNull(getNbtCompound()).setDouble(key, value);
    }

    @Override
    public void setByteArray(@NotNull String key, Byte[] value) {
        Objects.requireNonNull(getNbtCompound()).setByteArray(key, ArrayUtils.toPrimitive(value));
    }

    @Override
    public void setFloat(@NotNull String key, float value) {
        Objects.requireNonNull(getNbtCompound()).setFloat(key, value);
    }

    @Override
    public void setIntArray(@NotNull String key, int[] value) {
        Objects.requireNonNull(getNbtCompound()).setIntArray(key, value);
    }

    @Override
    public void setItemStack(@NotNull String key, @NotNull ItemStack value) {
        Objects.requireNonNull(getNbtCompound()).setItemStack(key, value);
    }

    @Override
    public void setLong(@NotNull String key, long value) {
        Objects.requireNonNull(getNbtCompound()).setLong(key, value);
    }

    @Override
    public void setShort(@NotNull String key, short value) {
        Objects.requireNonNull(getNbtCompound()).setShort(key, value);
    }

    @Override
    public void setString(@NotNull String key, @NotNull String value) {
        Objects.requireNonNull(getNbtCompound()).setString(key, value);
    }

    @Override
    public void setUUID(@NotNull String key, UUID value) {
        Objects.requireNonNull(getNbtCompound()).setUUID(key, value);
    }

    @Override
    public Set<String> getKeys() {
        return Objects.requireNonNull(getNbtCompound()).getKeys();
    }

    @Override
    public boolean hasKey(@NotNull String key) {
        return Objects.requireNonNull(getNbtCompound()).hasKey(key);
    }

    @Override
    public void removeKey(@NotNull String key) {
        Objects.requireNonNull(getNbtCompound()).removeKey(key);
    }

    @Override
    public void save() {

    }

    @Override
    public void delete() {
        for (String key : Objects.requireNonNull(getNbtCompound()).getKeys()) {
            Objects.requireNonNull(getNbtCompound()).removeKey(key);
        }
    }
}
