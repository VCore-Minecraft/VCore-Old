/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbt.holders.entity;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import de.verdox.vcorepaper.custom.nbt.holders.NBTHolderImpl;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class NBTEntityHolder extends NBTHolderImpl<Entity, NBTCompound> {
    private final NBTEntity nbtEntity;

    public NBTEntityHolder(Entity entity) {
        super(entity);
        this.nbtEntity = new NBTEntity(dataHolder);

    }

    @Override
    public NBTCompound getPersistentDataContainer() {
        return nbtEntity.getPersistentDataContainer();
    }

    @NotNull
    @Override
    public NBTCompound getVanillaCompound() {
        return nbtEntity;
    }
}
