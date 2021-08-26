/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.parts.local;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import de.verdox.vcore.synchronization.pipeline.datatypes.NetworkData;
import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.util.global.AnnotationResolver;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.06.2021 16:19
 */
public class LocalCacheImpl implements LocalCache {
    private final Map<Class<? extends VCoreData>, Map<UUID, VCoreData>> dataObjects = new ConcurrentHashMap<>();
    private final VCorePlugin<?, ?> plugin;

    public LocalCacheImpl(VCorePlugin<?, ?> plugin) {
        this.plugin = plugin;
        plugin.consoleMessage("&eLocalCache started", true);
    }

    @Override
    public <S extends VCoreData> S getData(@Nonnull Class<? extends S> dataClass, @Nonnull UUID objectUUID) {
        if (!dataExist(dataClass, objectUUID))
            return null;
        S data = dataClass.cast(dataObjects.get(dataClass).get(objectUUID));
        data.updateLastUse();
        return data;
    }

    @Override
    public <S extends VCoreData> Set<S> getAllData(@Nonnull Class<? extends S> dataClass) {
        return getSavedUUIDs(dataClass).stream().map(uuid -> getData(dataClass, uuid)).collect(Collectors.toSet());
    }

    @Override
    public <S extends VCoreData> void save(@Nonnull Class<? extends S> dataClass, @Nonnull S data) {
        if (dataExist(dataClass, data.getObjectUUID()))
            return;
        if (!dataObjects.containsKey(dataClass))
            dataObjects.put(dataClass, new ConcurrentHashMap<>());
        data.updateLastUse();
        dataObjects.get(dataClass).put(data.getObjectUUID(), data);
    }

    @Override
    public <S extends VCoreData> boolean dataExist(@Nonnull Class<? extends S> dataClass, @Nonnull UUID objectUUID) {
        if (!dataObjects.containsKey(dataClass))
            return false;
        return dataObjects.get(dataClass).containsKey(objectUUID);
    }

    @Override
    public <S extends VCoreData> boolean remove(@Nonnull Class<? extends S> dataClass, @Nonnull UUID objectUUID) {
        if (!dataExist(dataClass, objectUUID))
            return false;
        dataObjects.get(dataClass).remove(objectUUID);
        if (dataObjects.get(dataClass).size() == 0)
            dataObjects.remove(dataClass);
        return true;
    }

    @Override
    public <S extends VCoreData> Set<UUID> getSavedUUIDs(@Nonnull Class<? extends S> dataClass) {
        if (!dataObjects.containsKey(dataClass))
            return new HashSet<>();
        return dataObjects.get(dataClass).keySet();
    }

    @Override
    public <S extends VCoreData> S instantiateData(@Nonnull Class<? extends S> dataClass, @Nonnull UUID objectUUID) {
        // Network Data is not subsystem dependent
        if (!NetworkData.class.isAssignableFrom(dataClass)) {
            if (AnnotationResolver.findDependSubsystemClass(dataClass) == null)
                throw new NullPointerException(dataClass + " does not have RequiredSubsystem Annotation set.");
            VCoreSubsystem<?> subsystem = plugin.findDependSubsystem(dataClass);
            if (subsystem == null)
                throw new NullPointerException("RequiredSubsystem can't be null");
            if (!subsystem.isActivated())
                throw new NullPointerException("Provided Subsystem is not activated");
        }
        if (dataExist(dataClass, objectUUID))
            return getData(dataClass, objectUUID);

        try {
            S dataObject = dataClass.getDeclaredConstructor(VCorePlugin.class, UUID.class).newInstance(plugin, objectUUID);
            return dataClass.cast(dataObject);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}
