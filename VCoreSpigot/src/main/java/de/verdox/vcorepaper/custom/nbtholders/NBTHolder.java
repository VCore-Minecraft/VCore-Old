/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbtholders;

import de.tr7zw.changeme.nbtapi.NBTCompound;

public interface NBTHolder {
    void save();

    void delete();

    NBTCompound getPersistentDataContainer();

    NBTCompound getVanillaCompound();
}
