/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbt.holders;

import de.tr7zw.changeme.nbtapi.NBTCompound;

public interface NBTHolder<C extends NBTCompound> {
    void save();

    void delete();

    C getPersistentDataContainer();

    NBTCompound getVanillaCompound();
}
