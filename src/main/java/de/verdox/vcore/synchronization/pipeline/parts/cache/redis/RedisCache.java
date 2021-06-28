/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.parts.cache.redis;

import de.verdox.vcore.synchronization.pipeline.annotations.RequiredSubsystemInfo;
import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.interfaces.DataManipulator;
import de.verdox.vcore.synchronization.pipeline.interfaces.VCoreSerializable;
import de.verdox.vcore.synchronization.pipeline.parts.cache.GlobalCache;
import de.verdox.vcore.synchronization.pipeline.parts.storage.GlobalStorage;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.redisson.RedisConnection;
import org.redisson.api.*;
import org.redisson.codec.SerializationCodec;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.06.2021 15:49
 */
public class RedisCache extends RedisConnection implements GlobalCache {
    public RedisCache(@Nonnull VCorePlugin<?, ?> plugin, boolean clusterMode, @Nonnull String[] addressArray, String redisPassword) {
        super(plugin, clusterMode, addressArray, redisPassword);
        plugin.consoleMessage("&eRedis Manager started",true);
    }

    @Override
    public Map<String, Object> loadData(@Nonnull Class<? extends VCoreData> dataClass, @Nonnull UUID objectUUID) {
        return new HashMap<>(getObjectCache(dataClass,objectUUID));
    }

    @Override
    public void save(@Nonnull Class<? extends VCoreData> dataClass, @Nonnull UUID objectUUID, @Nonnull Map<String, Object> dataToSave) {
        getObjectCache(dataClass, objectUUID).putAll(dataToSave);
    }

    @Override
    public boolean remove(@Nonnull Class<? extends VCoreData> dataClass, @Nonnull UUID objectUUID) {
        RMap<String, Object> redisMap = (RMap<String, Object>) getObjectCache(dataClass, objectUUID);
        return redisMap.delete();
    }

    @Override
    public Map<String, Object> getObjectCache(Class<? extends VCoreData> dataClass, UUID objectUUID) {
        RMap<String, Object> objectCache = redissonClient.getMap(plugin.getPluginName()+"Cache:"+objectUUID+":"+ GlobalStorage.getDataStorageIdentifier(dataClass), new SerializationCodec());
        objectCache.expire(12, TimeUnit.HOURS);
        return objectCache;
    }

    @Override
    public Set<Map<String, Object>> getCacheList(Class<? extends VCoreData> dataClass) {
        Set<String> keys = getKeys(dataClass);
        Set<Map<String, Object>> set = new HashSet<>();
        keys.forEach(s -> set.add(redissonClient.getMap(s)));
        return set;
    }

    @Override
    public Set<String> getKeys(Class<? extends VCoreData> dataClass) {
        String pluginName = plugin.getPluginName();
        String mongoIdentifier = GlobalStorage.getDataStorageIdentifier(dataClass);
        return redissonClient.getKeys().getKeysStream().filter(s -> {
            String[] parts = s.split(":");
            return parts[0].equals(pluginName) && parts[2].equals(mongoIdentifier);
        }).collect(Collectors.toSet());
    }

    @Override
    public Set<UUID> getSavedUUIDs(@Nonnull Class<? extends VCoreData> dataClass) {
        return getKeys(dataClass).stream().map(s -> UUID.fromString(s.split(":")[1])).collect(Collectors.toSet());
    }

    @Override
    public boolean dataExist(@Nonnull Class<? extends VCoreData> dataClass, @Nonnull UUID objectUUID) {
        Map<String, Object> cache = getObjectCache(dataClass, objectUUID);

        Set<String> redisKeys = getObjectDataKeys(dataClass, objectUUID);

        return redisKeys.parallelStream().anyMatch(cache::containsKey);
    }

    @Override
    public DataManipulator constructDataManipulator(VCoreData vCoreData) {
        return new RedisDataManipulator(this, vCoreData);
    }

    private Set<String> getObjectDataKeys(Class<? extends VCoreData> vCoreDataClass, UUID uuid) {
        RequiredSubsystemInfo requiredSubsystemInfo = vCoreDataClass.getAnnotation(RequiredSubsystemInfo.class);
        if(requiredSubsystemInfo == null)
            throw new RuntimeException(getClass().getSimpleName()+" does not have RequiredSubsystemInfo Annotation set");
        if(uuid == null)
            return new HashSet<>();
        if(VCoreSerializable.getPersistentDataFieldNames(vCoreDataClass) == null)
            throw new NullPointerException(VCoreData.class.getSimpleName()+" does not provide RedisDataKeys");
        return VCoreSerializable.getPersistentDataFieldNames(vCoreDataClass);
    }

    public RTopic getTopic(Class<? extends VCoreData> dataClass, UUID objectUUID){
        if(dataClass == null)
            throw new NullPointerException("DataClass is null");
        if(objectUUID == null)
            throw new NullPointerException("objectUUID is null");
        String key = plugin.getPluginName()+"DataTopic:"+GlobalStorage.getDataStorageIdentifier(dataClass)+":"+objectUUID;
        return redissonClient.getTopic(key, new SerializationCodec());
    }
}
