/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.pipeline.parts.local;

import de.verdox.vcore.data.datatypes.PlayerData;
import de.verdox.vcore.data.datatypes.ServerData;
import de.verdox.vcore.data.datatypes.VCoreData;
import de.verdox.vcore.data.manager.PlayerSessionManager;
import de.verdox.vcore.data.manager.ServerDataManager;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import org.apache.commons.lang.NotImplementedException;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.06.2021 16:19
 */
public class LocalCacheImpl implements LocalCache{
    private final Map<Class<? extends VCoreData>, Map<UUID,VCoreData>> dataObjects = new ConcurrentHashMap<>();
    private final VCorePlugin<?, ?> plugin;

    public LocalCacheImpl(VCorePlugin<?,?> plugin){
        this.plugin = plugin;
    }

    @Override
    public <S extends VCoreData> S getData(@Nonnull Class<? extends S> dataClass, @Nonnull UUID objectUUID) {
        if(!exist(dataClass, objectUUID))
            throw new NullPointerException("No data in local cache with type "+dataClass+" and uuid "+objectUUID);
        return dataClass.cast(dataObjects.get(dataClass).get(objectUUID));
    }

    @Override
    public <S extends VCoreData> void save(@Nonnull Class<? extends S> dataClass, @Nonnull S data) {
        if(exist(dataClass,data.getUUID()))
            return;
        if(!dataObjects.containsKey(dataClass))
            dataObjects.put(dataClass,new ConcurrentHashMap<>());
        dataObjects.get(dataClass).put(data.getUUID(),data);
    }

    @Override
    public <S extends VCoreData> boolean exist(@Nonnull Class<? extends S> dataClass, @Nonnull UUID objectUUID) {
        if(!dataObjects.containsKey(dataClass))
            return false;
        return dataObjects.get(dataClass).containsKey(objectUUID);
    }

    @Override
    public <S extends VCoreData> boolean delete(@Nonnull Class<? extends S> dataClass, @Nonnull UUID objectUUID) {
        if(!exist(dataClass,objectUUID))
            return false;
        dataObjects.get(dataClass).remove(objectUUID);
        if(dataObjects.get(dataClass).size() == 0)
            dataObjects.remove(dataClass);
        return true;
    }

    @Override
    public <S extends VCoreData> Set<UUID> getSavedUUIDs(@Nonnull Class<? extends S> dataClass) {
        if(!dataObjects.containsKey(dataClass))
            return new HashSet<>();
        return dataObjects.get(dataClass).keySet();
    }

    @Override
    public <S extends VCoreData> S instantiateData(@Nonnull Class<? extends S> dataClass, @Nonnull UUID objectUUID) {
        if(VCorePlugin.findDependSubsystemClass(dataClass) == null)
            throw new NullPointerException(dataClass+" does not have RequiredSubsystem Annotation set.");
        VCoreSubsystem<?> subsystem = plugin.findDependSubsystem(dataClass);
        if(subsystem == null)
            throw new NullPointerException("RequiredSubsystem can't be null");
        if(!subsystem.isActivated())
            throw new NullPointerException("Provided Subsystem is not activated");
        if(exist(dataClass,objectUUID))
            return getData(dataClass,objectUUID);

        try {
            if(dataClass.isAssignableFrom(ServerData.class)){
                S dataObject =  dataClass.getDeclaredConstructor(ServerDataManager.class,UUID.class).newInstance(plugin.getServerDataManager(),objectUUID);
                return dataClass.cast(dataObject);
            }
            else if(dataClass.isAssignableFrom(PlayerData.class)){
                S dataObject =  dataClass.getDeclaredConstructor(PlayerSessionManager.class,UUID.class).newInstance(plugin.getSessionManager(),objectUUID);
                return dataClass.cast(dataObject);
            }
            throw new NotImplementedException("VCoreData was not integrated into data pipeline: "+dataClass.getName());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}
