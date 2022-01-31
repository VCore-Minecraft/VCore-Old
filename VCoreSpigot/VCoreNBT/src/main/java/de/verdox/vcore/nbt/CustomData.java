/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nbt;

import de.verdox.vcorepaper.custom.annotation.NBTIdentifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @param <T> Data to be stored
 */
public abstract class CustomData<T> {
    public CustomData() {
    }

    public List<String> asLabel(String valueAsString) {
        return List.of(valueAsString);
    }

    public T findInDataHolder(CustomDataHolder<?, ?, ?> customDataHolder) {
        String nbtKey = getNBTKey();
        if (!customDataHolder.toNBTHolder().getPersistentDataContainer().hasKey(nbtKey))
            return defaultValue();
        if (getTypeClass() == null)
            throw new NullPointerException("Can't return null!");
        return customDataHolder.toNBTHolder().getPersistentDataContainer().getObject(nbtKey, getTypeClass());
    }

    public void storeCustomData(CustomDataHolder<?, ?, ?> customDataHolder, T data) {
        customDataHolder.toNBTHolder().getPersistentDataContainer().setObject(getNBTKey(), data);
    }

    public boolean deleteData(CustomDataHolder<?, ?, ?> customDataHolder) {
        if (!customDataHolder.toNBTHolder().getPersistentDataContainer().hasKey(getNBTKey()))
            return false;
        customDataHolder.toNBTHolder().getPersistentDataContainer().removeKey(getNBTKey());
        return true;
    }

    @NotNull
    public abstract Class<T> getTypeClass();

    public abstract T defaultValue();

    @NotNull
    public String getNBTKey() {
        NBTIdentifier nbtIdentifier = getClass().getAnnotation(NBTIdentifier.class);
        if (nbtIdentifier == null)
            throw new IllegalStateException("Your CustomData Class " + getClass().getCanonicalName() + " needs to have the NBTIdentifier Annotation set!");
        return nbtIdentifier.nbtKey().toLowerCase();
    }
}
