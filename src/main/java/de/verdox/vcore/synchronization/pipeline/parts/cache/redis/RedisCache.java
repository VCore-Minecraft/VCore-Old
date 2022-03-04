/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.parts.cache.redis;

import com.google.gson.*;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.pipeline.annotations.VCoreDataProperties;
import de.verdox.vcore.synchronization.pipeline.datatypes.NetworkData;
import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.parts.cache.GlobalCache;
import de.verdox.vcore.synchronization.redisson.RedisConnection;
import de.verdox.vcore.util.global.AnnotationResolver;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RBucket;
import org.redisson.api.RTopic;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.SerializationCodec;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.06.2021 15:49
 */
public class RedisCache extends RedisConnection implements GlobalCache {

    private final Gson gson = new GsonBuilder().serializeNulls().create();

    public RedisCache(@NotNull VCorePlugin<?, ?> plugin, boolean clusterMode, @NotNull String[] addressArray, String redisPassword) {
        super(plugin, clusterMode, addressArray, redisPassword);
        plugin.consoleMessage("&eRedis Manager started", true);
    }

    @Override
    public JsonObject loadData(@Nonnull @NotNull Class<? extends VCoreData> dataClass, @Nonnull @NotNull UUID objectUUID) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");
        try {
            return JsonParser.parseString(getObjectCache(dataClass, objectUUID).get()).getAsJsonObject();
        } catch (Exception e) {
            plugin.consoleMessage("&cError while loading &b" + dataClass + " &cwith uuid &e" + objectUUID + " &7-> &4removing &7...", false);
            e.printStackTrace();
            remove(dataClass, objectUUID);
            return null;
        }
    }

    @Override
    public void save(@Nonnull @NotNull Class<? extends VCoreData> dataClass, @Nonnull @NotNull UUID objectUUID, @Nonnull @NotNull JsonElement dataToSave) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");

        RBucket<String> objectCache = getObjectCache(dataClass, objectUUID);
        objectCache.set(gson.toJson(dataToSave));
        updateExpireTime(dataClass, objectCache);
    }

    @Override
    public boolean remove(@Nonnull Class<? extends VCoreData> dataClass, @Nonnull @NotNull UUID objectUUID) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");

        RBucket<String> objectCache = getObjectCache(dataClass, objectUUID);
        return objectCache.delete();
    }

    public synchronized RBucket<String> getObjectCache(@Nonnull Class<? extends VCoreData> dataClass, @Nonnull @NotNull UUID objectUUID) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");
        String key;

        if (NetworkData.class.isAssignableFrom(dataClass))
            key = "VCoreCache:" + objectUUID + ":" + AnnotationResolver.getDataStorageIdentifier(dataClass);
        else
            key = plugin.getPluginName() + "Cache:" + objectUUID + ":" + AnnotationResolver.getDataStorageIdentifier(dataClass);
        RBucket<String> objectCache = redissonClient.getBucket(key, new StringCodec());
        updateExpireTime(dataClass, objectCache);

        return objectCache;
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
    public Set<UUID> getSavedUUIDs(@Nonnull @NotNull Class<? extends VCoreData> dataClass) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        return getKeys(dataClass).stream().map(s -> UUID.fromString(s.split(":")[1])).collect(Collectors.toSet());
    }

    @Override
    public boolean dataExist(@Nonnull @NotNull Class<? extends VCoreData> dataClass, @Nonnull @NotNull UUID objectUUID) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");
        return getObjectCache(dataClass, objectUUID).isExists();
    }

    private void updateExpireTime(@NotNull Class<? extends VCoreData> dataClass, RBucket<?> bucket) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        VCoreDataProperties properties = AnnotationResolver.getDataProperties(dataClass);

        if (bucket == null)
            return;

        if (properties.cleanOnNoUse()) {
            bucket.expire(properties.time(), properties.timeUnit());
        } else {
            bucket.expire(12, TimeUnit.HOURS);
        }
    }
}
