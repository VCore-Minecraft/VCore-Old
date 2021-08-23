/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbtholders;

import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.UUID;

public interface NBTHolder {
    ItemStack getItemStack(String key);

    <T> T getObject(String key, Class<T> type);

    UUID getUUID(String key);

    Long getLong(String key);

    String getString(String key);

    Double getDouble(String key);

    Integer getInteger(String key);

    Boolean getBoolean(String key);

    Byte getByte(String key);

    byte[] getByteArray(String key);

    Float getFloat(String key);

    void setObject(String key, Object value);

    void setBoolean(String key, boolean value);

    void setInteger(String key, int value);

    void setByte(String key, byte value);

    void setDouble(String key, double value);

    void setByteArray(String key, byte[] value);

    void setFloat(String key, float value);

    void setIntArray(String key, int[] value);

    void setItemStack(String key, ItemStack value);

    void setLong(String key, long value);

    void setShort(String key, short value);

    void setString(String key, String value);

    void setUUID(String key, UUID value);

    Set<String> getKeys();

    boolean hasKey(String key);

    void removeKey(String key);

    void save();

    void delete();

    NBTHolder getVanillaCompound();
}
