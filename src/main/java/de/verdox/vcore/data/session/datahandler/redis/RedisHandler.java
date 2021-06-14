/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.data.session.datahandler.redis;

import de.verdox.vcore.data.datatypes.VCoreData;
import de.verdox.vcore.data.session.DataSession;
import org.redisson.api.RMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 09.06.2021 14:38
 */
public abstract class RedisHandler <S extends VCoreData>{

    protected final DataSession<S> dataSession;

    public RedisHandler(DataSession<S> dataSession){
        this.dataSession = dataSession;
    }

    public abstract Set<String> getRedisKeys(Class<? extends S> vCoreDataClass, UUID uuid);
    public abstract RMap<String, Object> getRedisCache(Class<? extends S> dataClass, UUID uuid);
    public abstract boolean dataExistRedis(Class<? extends S> dataClass, UUID uuid);

    public final void redisToDatabase(Class<? extends S> dataClass,UUID objectUUID){
        if(dataClass == null)
            return;
        RMap<String, Object> redisCache = getRedisCache(dataClass, objectUUID);
        dataSession.getDatabaseHandler().saveToDatabase(dataClass,objectUUID,redisCache);
    }

    public final void redisToLocal(Class<? extends S> dataClass, UUID objectUUID){
        if(dataClass == null)
            return;
        if(objectUUID == null)
            return;
        RMap<String, Object> redisCache = getRedisCache(dataClass, objectUUID);
        S vCoreData = dataSession.getDataManager().instantiateVCoreData(dataClass,objectUUID);

        Set<String> redisKeys = getRedisKeys(dataClass,objectUUID);
        Map<String, Object> dataFromRedis = new HashMap<>();

        redisKeys.forEach(key -> dataFromRedis.put(key,redisCache.get(key)));

        vCoreData.restoreFromRedis(dataFromRedis);
        vCoreData.onLoad();

        dataSession.getLocalDataHandler().addDataLocally(vCoreData,dataClass,true);
    }
}
