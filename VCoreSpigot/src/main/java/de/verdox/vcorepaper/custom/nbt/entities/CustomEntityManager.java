/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbt.entities;

import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.CustomDataManager;
import de.verdox.vcorepaper.custom.workernpc.ProfessionRegistry;
import org.bukkit.entity.Entity;

import java.lang.reflect.InvocationTargetException;

public class CustomEntityManager extends CustomDataManager<Entity, EntityCustomData<?>, VCoreEntity> {

    private static CustomEntityManager instance = null;
    private final ProfessionRegistry professionRegistry = new ProfessionRegistry(this);

    public CustomEntityManager(VCorePaper plugin) {
        super(plugin);
        if (instance != null)
            throw new IllegalStateException("There can only be one CustomEntityManager");
        instance = this;
    }

    @Override
    public <U extends VCoreEntity> U wrap(Class<? extends U> type, Entity inputObject) {
        try {
            return type.getDeclaredConstructor(Entity.class, CustomEntityManager.class).newInstance(inputObject, this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public <U extends VCoreEntity> U convertTo(Class<? extends U> type, VCoreEntity customData) {
        try {
            return type.getDeclaredConstructor(Entity.class, CustomEntityManager.class).newInstance(customData.getDataHolder(), this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected EntityCustomData<?> instantiateCustomData(Class<? extends EntityCustomData<?>> dataClass) {
        try {
            return dataClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ProfessionRegistry getProfessionRegistry() {
        return professionRegistry;
    }
}
