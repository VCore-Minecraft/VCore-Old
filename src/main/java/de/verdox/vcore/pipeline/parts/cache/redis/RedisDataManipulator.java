/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.pipeline.parts.cache.redis;

import de.verdox.vcore.pipeline.annotations.DataContext;
import de.verdox.vcore.pipeline.datatypes.VCoreData;
import de.verdox.vcore.pipeline.interfaces.DataManipulator;
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
        this.messageListener = (channel, map) -> {
            vCoreData.deserialize(map);
        };
        dataTopic.addListener(Map.class,messageListener);
    }

    @Override
    public void cleanUp() {
        dataTopic.removeListener(messageListener);
    }

    @Override
    public void pushUpdate(VCoreData vCoreData, boolean async) {
        if(async)
            vCoreData.getPlugin().async(() -> doPush(vCoreData));
        else
            doPush(vCoreData);
    }

    private void doPush(VCoreData vCoreData){
        Map<String, Object> serialized = vCoreData.serialize();
        this.redisCache.save(vCoreData.getClass(),vCoreData.getObjectUUID(), serialized);
        dataTopic.publish(serialized);
    }
}
