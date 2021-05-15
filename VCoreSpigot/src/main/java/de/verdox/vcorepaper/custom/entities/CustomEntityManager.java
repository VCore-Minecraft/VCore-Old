package de.verdox.vcorepaper.custom.entities;

import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.CustomDataManager;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;

public class CustomEntityManager extends CustomDataManager<Entity, EntityCustomData<?>,VCoreEntity> {

    private static CustomEntityManager instance = null;

    public CustomEntityManager(VCorePaper plugin){
        super(plugin);
        if(instance != null)
            throw new IllegalStateException("There can only be one CustomEntityManager");
        instance = this;
    }

    @Override
    public VCoreEntity wrap(Class<? extends VCoreEntity> type, Entity inputObject){
        try {
            return type.getDeclaredConstructor(CustomEntityManager.class, Entity.class).newInstance(this,inputObject);
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
}
