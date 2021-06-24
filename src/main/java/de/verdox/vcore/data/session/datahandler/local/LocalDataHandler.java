/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.data.session.datahandler.local;

import de.verdox.vcore.pipeline.annotations.DataContext;
import de.verdox.vcore.data.datatypes.VCoreData;
import de.verdox.vcore.data.session.DataSession;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;

import java.util.Set;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 09.06.2021 13:48
 */
public abstract class LocalDataHandler <S extends VCoreData> {

    protected final DataSession<S> dataSession;

    public LocalDataHandler(DataSession<S> dataSession){
        this.dataSession = dataSession;
    }

    public abstract boolean dataExistLocally(Class<? extends S> dataClass, UUID uuid);
    public abstract void addDataLocally(S data, Class<? extends S> dataClass, boolean push);
    public abstract void removeDataLocally(Class<? extends S> dataClass, UUID uuid, boolean push);
    public abstract <T extends S> T getDataLocal(Class<? extends T> type, UUID uuid);
    public abstract <T extends S> Set<T> getAllLocalData(Class<? extends T> dataClass);
    public abstract Set<Object> getCachedKeys();

    public final void localToRedis(S dataObject, Class<? extends S> dataClass, UUID objectUUID){
        if(dataClass == null)
            return;
        if(objectUUID == null)
            return;
        // If it is a Local Datum it wont be published to redis again.
        // Method should not be invoked if local anyway
        if(dataSession.getDataManager().getRedisManager().getContext(dataClass).equals(DataContext.LOCAL))
            return;

        RMap<String, Object> redisCache = dataSession.getRedisHandler().getRedisCache(dataClass, objectUUID);

        dataObject.dataForRedis().forEach(redisCache::put);
        RTopic topic = dataObject.getDataTopic();

        long start = System.currentTimeMillis();
        topic.publishAsync(dataObject.dataForRedis());
        long end = System.currentTimeMillis() - start;
        dataSession.getDataManager().getPlugin().consoleMessage("&ePushing Update&7: &b"+objectUUID+" &7[&e"+end+" ms&7]",true);
    }

    public final void localToDatabase(Class<? extends S> dataClass,UUID objectUUID){
        dataSession.getDatabaseHandler().saveToDatabase(dataClass,objectUUID, getDataLocal(dataClass,objectUUID).dataForRedis());
    }
}
