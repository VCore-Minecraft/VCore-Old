package de.verdox.vcorepaper.custom.entities;

import de.tr7zw.changeme.nbtapi.NBTEntity;
import de.verdox.vcorepaper.custom.CustomData;
import de.verdox.vcorepaper.custom.CustomDataHolder;
import de.verdox.vcorepaper.custom.nbtholders.NBTEntityHolder;
import org.bukkit.entity.Entity;

import java.lang.reflect.InvocationTargetException;

public class VCoreEntity extends CustomDataHolder<Entity, NBTEntityHolder, CustomEntityManager> {

    public VCoreEntity(Entity entity, CustomEntityManager customEntityManager){
        super(entity, customEntityManager);
    }

    @Override
    protected <T, R extends CustomData<T>> void onStoreData(Class<? extends R> customDataType, T value) {

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

    @Override
    public NBTEntityHolder getNBTCompound() {
        return new NBTEntityHolder(getDataHolder());
    }
}
