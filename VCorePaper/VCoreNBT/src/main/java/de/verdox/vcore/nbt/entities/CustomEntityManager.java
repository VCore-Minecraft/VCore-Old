/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nbt.entities;

import de.verdox.vcore.nbt.CustomDataManager;
import de.verdox.vcore.nbt.VCoreNBTModule;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcorepaper.impl.plugin.VCorePaperPlugin;
import org.bukkit.entity.Entity;

import java.lang.reflect.InvocationTargetException;

public class CustomEntityManager extends CustomDataManager<Entity, de.verdox.vcore.nbt.entities.EntityCustomData<?>, de.verdox.vcore.nbt.entities.VCoreEntity> {

    public CustomEntityManager(VCoreNBTModule vCoreNBTModule, VCorePaperPlugin plugin) {
        super(vCoreNBTModule, plugin);
    }

    @Override
    public <U extends de.verdox.vcore.nbt.entities.VCoreEntity> U wrap(Class<? extends U> type, Entity inputObject) {
        try {
            return type.getDeclaredConstructor(Entity.class, CustomEntityManager.class).newInstance(inputObject, this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public <U extends de.verdox.vcore.nbt.entities.VCoreEntity> U convertTo(Class<? extends U> type, VCoreEntity customData) {
        try {
            return type.getDeclaredConstructor(Entity.class, CustomEntityManager.class).newInstance(customData.getDataHolder(), this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected de.verdox.vcore.nbt.entities.EntityCustomData<?> instantiateCustomData(Class<? extends EntityCustomData<?>> dataClass) {
        try {
            return dataClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}
