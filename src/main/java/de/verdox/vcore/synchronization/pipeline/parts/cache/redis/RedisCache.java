/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.parts.cache.redis;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.pipeline.datatypes.NetworkData;
import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.parts.manipulator.DataSynchronizer;
import de.verdox.vcore.synchronization.pipeline.interfaces.VCoreSerializable;
import de.verdox.vcore.synchronization.pipeline.parts.cache.GlobalCache;
import de.verdox.vcore.synchronization.pipeline.parts.manipulator.redis.RedisDataSynchronizer;
import de.verdox.vcore.synchronization.redisson.RedisConnection;
import de.verdox.vcore.util.global.AnnotationResolver;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;
import org.redisson.client.codec.ByteArrayCodec;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.codec.CborJacksonCodec;
import org.redisson.codec.SerializationCodec;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.06.2021 15:49
 */
public class RedisCache extends RedisConnection implements GlobalCache {

    public RedisCache(@NotNull VCorePlugin<?, ?> plugin, boolean clusterMode, @NotNull String[] addressArray, String redisPassword) {
        super(plugin, clusterMode, addressArray, redisPassword);
        plugin.consoleMessage("&eRedis Manager started", true);
    }

    @Override
    public Map<String, Object> loadData(@Nonnull @NotNull Class<? extends VCoreData> dataClass, @Nonnull @NotNull UUID objectUUID) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");
        try {
            return new HashMap<>(getObjectCache(dataClass, objectUUID));
        } catch (Exception e) {
            plugin.consoleMessage("&cError while loading &b" + dataClass + " &cwith uuid &e" + objectUUID + " &7-> &4removing &7...", false);
            remove(dataClass, objectUUID);
            return null;
        }
    }

    @Override
    public void save(@Nonnull @NotNull Class<? extends VCoreData> dataClass, @Nonnull @NotNull UUID objectUUID, @Nonnull @NotNull Map<String, Object> dataToSave) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");
        Map<String, Object> objectCache = getObjectCache(dataClass, objectUUID);
        dataToSave.forEach((key, value) -> {
            if (value == null)
                objectCache.remove(key);
            else
                objectCache.put(key, value);
        });
    }

    @Override
    public boolean remove(@Nonnull @NotNull Class<? extends VCoreData> dataClass, @Nonnull @NotNull UUID objectUUID) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");
        RMap<String, Object> redisMap = (RMap<String, Object>) getObjectCache(dataClass, objectUUID);
        return redisMap.delete();
    }

    @Override
    public Map<String, Object> getObjectCache(Class<? extends VCoreData> dataClass, UUID objectUUID) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");
        RMap<String, Object> objectCache;
        if (NetworkData.class.isAssignableFrom(dataClass))
            objectCache = redissonClient.getMap("VCoreCache:" + objectUUID + ":" + AnnotationResolver.getDataStorageIdentifier(dataClass), new JsonJacksonCodec(dataClass.getClassLoader()));
        else {
            objectCache = redissonClient.getMap(plugin.getPluginName() + "Cache:" + objectUUID + ":" + AnnotationResolver.getDataStorageIdentifier(dataClass), new JsonJacksonCodec(dataClass.getClassLoader()));
            objectCache.expire(12, TimeUnit.HOURS);
        }
        return objectCache;
    }

    @Override
    public Set<Map<String, Object>> getCacheList(Class<? extends VCoreData> dataClass) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Set<String> keys = getKeys(dataClass);
        Set<Map<String, Object>> set = new HashSet<>();
        keys.forEach(s -> set.add(redissonClient.getMap(s, new JsonJacksonCodec(dataClass.getClassLoader()))));
        return set;
    }

    @Override
    public Set<String> getKeys(Class<? extends VCoreData> dataClass) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        String pluginName = plugin.getPluginName();
        String mongoIdentifier = AnnotationResolver.getDataStorageIdentifier(dataClass);
        return redissonClient.getKeys().getKeysStream().filter(s -> {
            String[] parts = s.split(":");
            return parts[0].equals(pluginName) && parts[2].equals(mongoIdentifier);
        }).collect(Collectors.toSet());
    }

    @Override
    public Map<String, Object> getGlobalCacheMap(String name) {
        Objects.requireNonNull(name, "name can't be null!");
        RMap<String, Object> objectCache = redissonClient.getMap("InternalVCoreData:" + name, new JsonJacksonCodec(name.getClass().getClassLoader()));
        objectCache.expire(1, TimeUnit.HOURS);
        return objectCache;
    }

    @Override
    public Set<UUID> getSavedUUIDs(@Nonnull @NotNull Class<? extends VCoreData> dataClass) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        return getKeys(dataClass).stream().map(s -> UUID.fromString(s.split(":")[1])).collect(Collectors.toSet());
    }

    @Override
    public boolean dataExist(@Nonnull @NotNull Class<? extends VCoreData> dataClass, @Nonnull @NotNull UUID objectUUID) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");
        Map<String, Object> cache = getObjectCache(dataClass, objectUUID);

        Set<String> redisKeys = getObjectDataKeys(dataClass, objectUUID);
        return redisKeys.parallelStream().anyMatch(cache::containsKey);
    }

    private Set<String> getObjectDataKeys(@NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");
        if (VCoreSerializable.getPersistentDataFieldNames(dataClass) == null)
            throw new NullPointerException(VCoreData.class.getSimpleName() + " does not provide RedisDataKeys");
        return VCoreSerializable.getPersistentDataFieldNames(dataClass);
    }

    public RTopic getTopic(@NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");
        String key = plugin.getPluginName() + "DataTopic:" + AnnotationResolver.getDataStorageIdentifier(dataClass) + ":" + objectUUID;
        return redissonClient.getTopic(key, new SerializationCodec());
    }

    public RTopic getTopic(@NotNull Class<? extends VCoreData> dataClass) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        String key = plugin.getPluginName() + "DataTopic:" + AnnotationResolver.getDataStorageIdentifier(dataClass);
        return redissonClient.getTopic(key, new SerializationCodec());
    }
}
