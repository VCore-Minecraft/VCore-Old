/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.entities;

import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.CustomData;
import de.verdox.vcorepaper.custom.CustomDataHolder;
import de.verdox.vcorepaper.custom.nbtholders.NBTEntityHolder;
import de.verdox.vcorepaper.custom.nbtholders.NBTHolder;
import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;

public class VCoreEntity extends CustomDataHolder<Entity, NBTEntityHolder, CustomEntityManager> {

    public VCoreEntity(@Nonnull Entity entity, @Nonnull CustomEntityManager customEntityManager) {
        super(entity, customEntityManager);
    }

    @Override
    protected <T, R extends CustomData<T>> void onStoreData(Class<? extends R> customDataType, T value) {
        VCorePaper.getInstance().consoleMessage("&eStoring&7 &a" + customDataType + ": &b" + value, true);
        getNBTCompound().getKeys().forEach(s -> VCorePaper.getInstance().consoleMessage(s, 2, true));
    }

    @Override
    protected <T, R extends CustomData<T>> R instantiateData(Class<? extends R> customDataType) {
        try {
            return customDataType.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nonnull
    @Override
    public NBTEntityHolder getNBTCompound() {
        return new NBTEntityHolder(getDataHolder());
    }

    public NBTHolder getVanillaCompound() {
        return getNBTCompound().getVanillaCompound();
    }


}
