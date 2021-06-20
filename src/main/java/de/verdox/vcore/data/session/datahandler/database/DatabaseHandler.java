/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.data.session.datahandler.database;

import de.verdox.vcore.data.datatypes.VCoreData;
import de.verdox.vcore.data.session.DataSession;
import org.redisson.api.RMap;

import java.util.Map;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 09.06.2021 14:27
 */
public abstract class DatabaseHandler <S extends VCoreData> {

    protected final DataSession<S> dataSession;

    public DatabaseHandler(DataSession<S> dataSession){
        this.dataSession = dataSession;
    }

    public abstract Map<String, Object> loadDataFromDatabase(Class<? extends S> dataClass, UUID objectUUID);
    public abstract boolean dataExistInDatabase(Class<? extends S> dataClass, UUID objectUUID);
    public abstract void saveToDatabase(Class<? extends S> dataClass, UUID objectUUID, Map<String, Object> dataToSave);

    public final void dataBaseToRedis(Class<? extends S> dataClass, UUID objectUUID){
        S vCoreData = dataSession.getDataManager().instantiateVCoreData(dataClass,objectUUID);
        RMap<String, Object> redisCache = dataSession.getRedisHandler().getRedisCache(dataClass,objectUUID);

        Map<String, Object> dataFromDatabase = loadDataFromDatabase(dataClass,objectUUID);

        vCoreData.dataForRedis().forEach(redisCache::put);
        vCoreData.restoreFromRedis(dataFromDatabase);
        vCoreData.onLoad();
    }

    public final void databaseToLocal(Class<? extends S> dataClass, UUID objectUUID){
        S vCoreData = dataSession.getDataManager().instantiateVCoreData(dataClass,objectUUID);

        Map<String, Object> dataFromDatabase = loadDataFromDatabase(dataClass,objectUUID);
        vCoreData.restoreFromDataBase(dataFromDatabase);
        dataSession.getLocalDataHandler().addDataLocally(vCoreData, dataClass, true);
    }

}
