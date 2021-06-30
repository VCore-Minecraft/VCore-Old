/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.parts.cache.redis;

import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.interfaces.DataManipulator;
import de.verdox.vcore.synchronization.pipeline.parts.DataSynchronizer;
import org.redisson.api.RTopic;
import org.redisson.api.listener.MessageListener;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 01:24
 */
public class RedisDataManipulator implements DataManipulator {

    private final RedisCache redisCache;
    private final RTopic dataTopic;
    private final MessageListener<Map<String,Object>> messageListener;

    RedisDataManipulator(@Nonnull RedisCache redisCache, VCoreData vCoreData){
        this.redisCache = redisCache;
        this.dataTopic = this.redisCache.getTopic(vCoreData.getClass(), vCoreData.getObjectUUID());
        this.messageListener = (channel, map) -> vCoreData.deserialize(map);
        dataTopic.addListener(Map.class,messageListener);
    }

    @Override
    public void cleanUp() {
        dataTopic.removeListener(messageListener);
    }

    @Override
    public void pushUpdate(VCoreData vCoreData, Runnable callback) {
        doPush(vCoreData, callback);
    }

    private void doPush(VCoreData vCoreData, Runnable callback){
        dataTopic.publish(vCoreData.serialize());
        vCoreData.getPlugin().getServices().getPipeline().getSynchronizer().synchronize(DataSynchronizer.DataSourceType.LOCAL, DataSynchronizer.DataSourceType.GLOBAL_CACHE, vCoreData.getClass(), vCoreData.getObjectUUID());
        callback.run();
    }
}
