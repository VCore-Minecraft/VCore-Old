/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbtholders.entity;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import de.verdox.vcorepaper.custom.nbtholders.NBTHolder;
import de.verdox.vcorepaper.custom.nbtholders.NBTHolderImpl;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class NBTEntityHolder extends NBTHolderImpl<Entity> {

    public NBTEntityHolder(Entity entity) {
        super(entity);

    }

    @Override
    protected NBTCompound getNbtCompound() {
        return new NBTEntity(dataHolder).getPersistentDataContainer();
    }

    @NotNull
    @Override
    public NBTHolder getVanillaCompound() {
        return new NBTHolderImpl<>(dataHolder) {
            @Override
            protected NBTCompound getNbtCompound() {
                return new NBTEntity(dataHolder);
            }

            @Override
            public NBTHolder getVanillaCompound() {
                return this;
            }
        };
    }
}
