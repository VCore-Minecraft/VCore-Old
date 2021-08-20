/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom;

import de.verdox.vcorepaper.custom.annotation.NBTIdentifier;

import javax.annotation.Nonnull;
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
        if (!customDataHolder.getNBTCompound().hasKey(nbtKey))
            return defaultValue();
        if (getTypeClass() == null)
            throw new NullPointerException("Can't return null!");
        return customDataHolder.getNBTCompound().getObject(nbtKey, getTypeClass());
    }

    public void storeCustomData(CustomDataHolder<?, ?, ?> customDataHolder, T data) {
        customDataHolder.getNBTCompound().setObject(getNBTKey(), data);
    }

    public boolean deleteData(CustomDataHolder<?, ?, ?> customDataHolder) {
        if (!customDataHolder.getNBTCompound().hasKey(getNBTKey()))
            return false;
        customDataHolder.getNBTCompound().removeKey(getNBTKey());
        return true;
    }

    @Nonnull
    public abstract Class<T> getTypeClass();

    public abstract T defaultValue();

    @Nonnull
    public String getNBTKey() {
        NBTIdentifier nbtIdentifier = getClass().getAnnotation(NBTIdentifier.class);
        if (nbtIdentifier == null)
            throw new IllegalStateException("Your CustomData Class " + getClass().getCanonicalName() + " needs to have the NBTIdentifier Annotation set!");
        return nbtIdentifier.nbtKey().toLowerCase();
    }
}
