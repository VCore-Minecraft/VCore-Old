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
import de.verdox.vcore.synchronization.pipeline.parts.manipulator.SynchronizingService;
import de.verdox.vcore.synchronization.pipeline.parts.manipulator.redis.RedisSynchronizingService;
import de.verdox.vcore.synchronization.pipeline.parts.storage.GlobalStorage;
import de.verdox.vcore.synchronization.pipeline.parts.storage.json.JsonFileStorage;
import de.verdox.vcore.synchronization.pipeline.parts.storage.mongodb.MongoDBStorage;
import de.verdox.vcore.synchronization.redisson.RedisConnection;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
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
        SynchronizingService synchronizingService = null;
        GlobalCache globalCache = null;
        GlobalStorage globalStorage = null;

        if (isGlobalCacheEnabled()) {
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
        }

        if (isDataSynchronizerEnabled()) {

            if (useCoreDataSynchronizer()) {
                synchronizingService = plugin.getCoreInstance().getServices().getPipeline().getSynchronizingService();
                plugin.consoleMessage("&eSelecting Core DataSynchronizer", false);
            } else {
                String dataSynchronizerType = config.getString("DataSynchronizer.type");
                plugin.consoleMessage("&DataSynchronizer&7: &b" + dataSynchronizerType, false);

                if (dataSynchronizerType.equalsIgnoreCase("redis")) {
                    boolean useCluster = config.getBoolean("DataSynchronizer.redis.useCluster");
                    String[] addresses = config.getStringList("DataSynchronizer.redis.addresses").toArray(new String[0]);
                    String password = config.getString("DataSynchronizer.redis.password");

                    RedisConnection redisConnection = new RedisConnection(plugin, useCluster, addresses, password);
                    synchronizingService = new RedisSynchronizingService(localCache, redisConnection);
                }
            }
        }

        if (isGlobalStorageEnabled()) {
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
                } else if (globalStorageType.equals("json")) {
                    String pathValue = config.getString("GlobalStorage.json.path");
                    Path path = pathValue != null && !pathValue.isBlank() ? Path.of(pathValue) : Path.of(plugin.getPluginDataFolder().getAbsolutePath() + "//" + "storage");

                    globalStorage = new JsonFileStorage(path);
                } else {
                    plugin.consoleMessage("&cNo GlobalStorage loaded&7!", false);
                }
            }
        }

        return new PipelineImpl(plugin, localCache, synchronizingService, globalCache, globalStorage);
    }

    public boolean useCoreGlobalCache() {
        boolean useCoreInstance = config.getBoolean("GlobalStorage.useCoreInstance");
        if (plugin.equals(plugin.getCoreInstance()))
            useCoreInstance = false;
        return useCoreInstance;
    }

    public boolean useCoreDataSynchronizer() {
        boolean useCoreInstance = config.getBoolean("DataSynchronizer.useCoreInstance");
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

    public boolean isGlobalCacheEnabled() {
        return config.getBoolean("GlobalCache.enable");
    }

    public boolean isDataSynchronizerEnabled() {
        return config.getBoolean("DataSynchronizer.enable");
    }

    public boolean isGlobalStorageEnabled() {
        return config.getBoolean("GlobalStorage.enable");
    }

    @Override
    public void onInit() {
        getPlugin().consoleMessage("&6Initializing PipelineConfig " + config.getFilePath(), true);
    }

    @Override
    public void setupConfig() {

        // ### GLOBAL CACHE ###

        config.addDefault("GlobalCache.enable", false);
        config.addDefault("GlobalCache.type", "redis");
        if (!plugin.equals(plugin.getCoreInstance()))
            config.addDefault("GlobalCache.useCoreInstance", true);
        config.addDefault("GlobalCache.redis.useCluster", false);
        config.addDefault("GlobalCache.redis.addresses", List.of("redis://localhost:6379"));
        config.addDefault("GlobalCache.redis.password", "");

        // ### DATA SYNCHRONIZER ###

        config.addDefault("DataSynchronizer.enable", false);
        config.addDefault("DataSynchronizer.type", "redis");
        if (!plugin.equals(plugin.getCoreInstance()))
            config.addDefault("DataSynchronizer.useCoreInstance", true);
        config.addDefault("DataSynchronizer.redis.useCluster", false);
        config.addDefault("DataSynchronizer.redis.addresses", List.of("redis://localhost:6379"));
        config.addDefault("DataSynchronizer.redis.password", "");

        // ### GLOBAL STORAGE ###

        config.addDefault("GlobalStorage.enable", true);
        config.addDefault("GlobalStorage.type", "mongodb");
        if (!plugin.equals(plugin.getCoreInstance()))
            config.addDefault("GlobalStorage.useCoreInstance", true);

        config.addDefault("GlobalStorage.json.path", "");

        config.addDefault("GlobalStorage.mongodb.host", "127.0.0.1");
        config.addDefault("GlobalStorage.mongodb.port", 27017);
        config.addDefault("GlobalStorage.mongodb.database", "vcore");
        config.addDefault("GlobalStorage.mongodb.user", "");
        config.addDefault("GlobalStorage.mongodb.password", "");
        save();
    }
}
