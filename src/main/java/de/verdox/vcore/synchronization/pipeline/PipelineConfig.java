/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.files.config.VCoreYAMLConfig;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import de.verdox.vcore.synchronization.pipeline.parts.cache.GlobalCache;
import de.verdox.vcore.synchronization.pipeline.parts.cache.redis.RedisCache;
import de.verdox.vcore.synchronization.pipeline.parts.local.LocalCache;
import de.verdox.vcore.synchronization.pipeline.parts.local.LocalCacheImpl;
import de.verdox.vcore.synchronization.pipeline.parts.storage.GlobalStorage;
import de.verdox.vcore.synchronization.pipeline.parts.storage.mongodb.MongoDBStorage;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 23:00
 */
public class PipelineConfig extends VCoreYAMLConfig {

    public PipelineConfig(VCorePlugin<?, ?> plugin, String fileName, String pluginDirectory) {
        super(plugin, fileName, pluginDirectory);
    }

    //TODO: Wenn Verbindungen nicht hergestellt werden können, SpielerConnect mit geeigneter Nachricht abbrechen

    public Pipeline constructPipeline(@NotNull VCorePlugin<?, ?> plugin) {
        Objects.requireNonNull(plugin, "plugin can't be null!");
        plugin.consoleMessage("&eConstructing Pipeline", false);
        LocalCache localCache = new LocalCacheImpl(plugin);
        GlobalCache globalCache = null;
        GlobalStorage globalStorage = null;

        // Initializing Global Cache
        if (useCoreGlobalCache()) {
            globalCache = plugin.getCoreInstance().getServices().getPipeline().getGlobalCache();
            plugin.consoleMessage("&eSelecting Core GlobalCache", false);
        } else {
            String globalCacheType = config.getString("GlobalCache.type");
            plugin.consoleMessage("&eGlobalCacheType&7: &b" + globalCacheType, false);
            if (globalCacheType.equalsIgnoreCase("redis")) {
                boolean useCluster = config.getBoolean("GlobalCache.redis.useCluster");
                String[] addresses = config.getStringList("GlobalCache.redis.addresses").toArray(new String[0]);
                String password = config.getString("GlobalCache.redis.password");

                globalCache = new RedisCache(plugin, useCluster, addresses, password);
            } else {
                plugin.consoleMessage("&cNo GlobalCache loaded&7!", false);
            }
        }

        // Initializing Global Storage
        if (useCoreGlobalStorage()) {
            globalStorage = plugin.getCoreInstance().getServices().getPipeline().getGlobalStorage();
            plugin.consoleMessage("&eSelecting Core GlobalStorage", false);
        } else {
            String globalStorageType = config.getString("GlobalStorage.type");
            plugin.consoleMessage("&eGlobalStorageType&7: &b" + globalStorageType, false);
            if (globalStorageType.equalsIgnoreCase("mongoDB")) {

                String host = config.getString("GlobalStorage.mongodb.host");
                int port = config.getInt("GlobalStorage.mongodb.port");
                String database = config.getString("GlobalStorage.mongodb.database");
                String user = config.getString("GlobalStorage.mongodb.user");
                String password = config.getString("GlobalStorage.mongodb.password");

                globalStorage = new MongoDBStorage(plugin, host, database, port, user, password);
            } else {
                plugin.consoleMessage("&cNo GlobalStorage loaded&7!", false);
            }
        }
        return new PipelineManager(plugin, localCache, globalCache, globalStorage);
    }

    public boolean useCoreGlobalCache() {
        boolean useCoreInstance = config.getBoolean("GlobalStorage.useCoreInstance");
        if (plugin.equals(plugin.getCoreInstance()))
            useCoreInstance = false;
        return useCoreInstance;
    }

    public boolean useCoreGlobalStorage() {
        boolean useCoreInstance = config.getBoolean("GlobalStorage.useCoreInstance");
        if (plugin.equals(plugin.getCoreInstance()))
            useCoreInstance = false;
        return useCoreInstance;
    }

    @Override
    public void onInit() {
        getPlugin().consoleMessage("&6Initializing PipelineConfig " + config.getFilePath(), true);
    }

    @Override
    public void setupConfig() {
        config.addDefault("GlobalCache.type", "redis");
        config.addDefault("GlobalCache.useCoreInstance", true);
        config.addDefault("GlobalCache.redis.useCluster", false);
        config.addDefault("GlobalCache.redis.addresses", List.of("redis://localhost:6379"));
        config.addDefault("GlobalCache.redis.password", "");

        config.addDefault("GlobalStorage.type", "mongodb");
        config.addDefault("GlobalStorage.useCoreInstance", true);
        config.addDefault("GlobalStorage.mongodb.host", "127.0.0.1");
        config.addDefault("GlobalStorage.mongodb.port", 27017);
        config.addDefault("GlobalStorage.mongodb.database", "vcore");
        config.addDefault("GlobalStorage.mongodb.user", "");
        config.addDefault("GlobalStorage.mongodb.password", "");
        save();
    }
}
