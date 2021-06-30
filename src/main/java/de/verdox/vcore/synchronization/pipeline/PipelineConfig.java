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

import java.io.File;
import java.util.List;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 23:00
 */
public class PipelineConfig extends VCoreYAMLConfig {

    public PipelineConfig(VCorePlugin<?, ?> plugin, String fileName, String pluginDirectory) {
        super(plugin, fileName, pluginDirectory);
    }

    public Pipeline constructPipeline(VCorePlugin<?,?> plugin){
        plugin.consoleMessage("&eConstructing Pipeline",false);
        LocalCache localCache = new LocalCacheImpl(plugin);
        GlobalCache globalCache = null;
        GlobalStorage globalStorage = null;

        String globalCacheType = config.getString("GlobalCache.type");
        plugin.consoleMessage("&eGlobalCacheType&7: &b"+globalCacheType,false);
        if(globalCacheType.equalsIgnoreCase("redis")){
            boolean useCluster = config.getBoolean("GlobalCache.redis.useCluster");
            String[] addresses = config.getStringList("GlobalCache.redis.addresses").toArray(new String[0]);
            String password = config.getString("GlobalCache.redis.password");
            globalCache = new RedisCache(plugin,useCluster,addresses,password);
        }
        String globalStorageType = config.getString("GlobalStorage.type");
        plugin.consoleMessage("&eGlobalStorageType&7: &b"+globalStorageType,false);
        if(globalStorageType.equalsIgnoreCase("mongoDB")){

            String host = config.getString("GlobalStorage.mongodb.host");
            int port = config.getInt("GlobalStorage.mongodb.port");
            String database = config.getString("GlobalStorage.mongodb.database");
            String user = config.getString("GlobalStorage.mongodb.user");
            String password = config.getString("GlobalStorage.mongodb.password");

            globalStorage = new MongoDBStorage(plugin, host, database, port, user, password);
        }
        return new PipelineManager(plugin, localCache, globalCache, globalStorage);
    }

    @Override
    public void onInit() {
        getPlugin().consoleMessage("&6Initializing PipelineConfig "+config.getFilePath(),true);
    }

    @Override
    public void setupConfig() {
        config.addDefault("GlobalCache.type","redis");
        config.addDefault("GlobalCache.redis.useCluster", false);
        config.addDefault("GlobalCache.redis.addresses", List.of("redis://localhost:6379"));
        config.addDefault("GlobalCache.redis.password", "");

        config.addDefault("GlobalStorage.type","mongodb");
        config.addDefault("GlobalStorage.mongodb.host","127.0.0.1");
        config.addDefault("GlobalStorage.mongodb.port",27017);
        config.addDefault("GlobalStorage.mongodb.database","vcore");
        config.addDefault("GlobalStorage.mongodb.user","");
        config.addDefault("GlobalStorage.mongodb.password","");
        save();
    }
}
