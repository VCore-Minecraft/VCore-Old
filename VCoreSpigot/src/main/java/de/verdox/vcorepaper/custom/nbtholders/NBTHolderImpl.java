/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbtholders;

import de.tr7zw.changeme.nbtapi.NBTCompound;

import java.util.Objects;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 23.08.2021 13:57
 */
public abstract class NBTHolderImpl<T, C extends NBTCompound> implements NBTHolder<C> {

    protected final T dataHolder;

    public NBTHolderImpl(T dataHolder) {
        this.dataHolder = dataHolder;
    }

    @Override
    public void save() {

    }

    @Override
    public void delete() {
        for (String key : Objects.requireNonNull(getPersistentDataContainer().getKeys())) {
            Objects.requireNonNull(getPersistentDataContainer()).removeKey(key);
        }
    }
}
