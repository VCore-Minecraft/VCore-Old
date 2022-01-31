/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nbt.holders;

import de.tr7zw.changeme.nbtapi.NBTCompound;

public interface NBTHolder<C extends NBTCompound> {
    void save();

    void delete();

    C getPersistentDataContainer();

    NBTCompound getVanillaCompound();
}
