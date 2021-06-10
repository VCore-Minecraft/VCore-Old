/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.data.session.datahandler.redis;

import de.verdox.vcore.data.annotations.RequiredSubsystemInfo;
import de.verdox.vcore.data.datatypes.VCoreData;
import de.verdox.vcore.data.session.DataSession;
import org.redisson.api.RMap;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 09.06.2021 14:41
 */
public class RedisHandlerImpl <S extends VCoreData> extends RedisHandler<S>{
    public RedisHandlerImpl(DataSession<S> dataSession) {
        super(dataSession);
    }

    @Override
    public Set<String> getRedisKeys(Class<? extends S> vCoreDataClass, UUID uuid) {
        RequiredSubsystemInfo requiredSubsystemInfo = vCoreDataClass.getAnnotation(RequiredSubsystemInfo.class);
        if(requiredSubsystemInfo == null)
            throw new RuntimeException(getClass().getSimpleName()+" does not have RequiredSubsystemInfo Annotation set");
        if(uuid == null)
            return new HashSet<>();
        if(VCoreData.getRedisDataKeys(vCoreDataClass) == null)
            throw new NullPointerException(VCoreData.class.getSimpleName()+" does not provide RedisDataKeys");
        return VCoreData.getRedisDataKeys(vCoreDataClass);
    }

    @Override
    public RMap<String, Object> getRedisCache(Class<? extends S> dataClass) {
        return dataSession.getDataManager().getRedisManager().getRedisCache(dataClass,dataSession.getUuid());
    }

    @Override
    public boolean dataExistRedis(Class<? extends S> dataClass, UUID uuid) {
        RMap<String, Object> redisCache = getRedisCache(dataClass);

        Set<String> redisKeys = getRedisKeys(dataClass,uuid);

        return redisKeys.parallelStream().anyMatch(redisCache::containsKey);
    }
}
