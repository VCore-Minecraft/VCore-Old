/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbtholders;

import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.UUID;

public interface NBTHolder {
    Boolean getBoolean(String key);

    Integer getInteger(String key);

    Double getDouble(String key);

    String getString(String key);

    UUID getUUID(String key);

    Long getLong(String key);

    ItemStack getItemStack(String key);

    <T> T getObject(String key, Class<T> type);

    void setObject(String key, Object value);

    Set<String> getKeys();

    boolean hasKey(String key);

    void removeKey(String key);

    void save();
}
