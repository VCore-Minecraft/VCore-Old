package de.verdox.vcorepaper.custom.entities;

import de.tr7zw.changeme.nbtapi.NBTEntity;
import de.verdox.vcorepaper.custom.CustomDataHolder;
import org.bukkit.entity.Entity;

import java.lang.reflect.InvocationTargetException;

public class VCoreEntity extends CustomDataHolder<Entity, NBTEntity> {

    private final CustomEntityManager customEntityManager;

    public VCoreEntity(Entity entity, CustomEntityManager customEntityManager){
        super(entity);
        this.customEntityManager = customEntityManager;
    }

    @Override
    protected <T, R extends EntityCustomData<T>> R instantiateData(Class<? extends R> customDataType) {
        try {
            return customDataType.getDeclaredConstructor(CustomEntityManager.class, VCoreEntity.class).newInstance(customEntityManager,this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public NBTEntity getNBTCompound() {
        return new NBTEntity(getDataHolder());
    }
}
