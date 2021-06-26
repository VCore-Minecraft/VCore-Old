/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.files.json.JsonConfig;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import de.verdox.vcore.synchronization.pipeline.parts.cache.GlobalCache;
import de.verdox.vcore.synchronization.pipeline.parts.cache.redis.RedisCache;
import de.verdox.vcore.synchronization.pipeline.parts.local.LocalCache;
import de.verdox.vcore.synchronization.pipeline.parts.local.LocalCacheImpl;
import de.verdox.vcore.synchronization.pipeline.parts.storage.GlobalStorage;
import de.verdox.vcore.synchronization.pipeline.parts.storage.mongodb.MongoDBStorage;

import java.io.File;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 23:00
 */
public class VCorePipelineConfig extends JsonConfig {
    public VCorePipelineConfig(File folder, File file) {
        super(folder, file);
    }

    public Pipeline constructPipeline(VCorePlugin<?,?> plugin){
        System.out.println("Constructing");
        LocalCache localCache = new LocalCacheImpl(plugin);
        GlobalCache globalCache = new RedisCache(plugin,false,new String[]{"redis://localhost:6379"},"");
        GlobalStorage globalStorage = new MongoDBStorage(plugin, "127.0.0.1", "vcore", 27017, "", "");;
        save();
        return new PipelineManager(plugin, localCache, globalCache, globalStorage);
    }
}
