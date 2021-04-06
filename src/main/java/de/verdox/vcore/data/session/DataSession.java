package de.verdox.vcore.data.session;

import com.mongodb.client.MongoCollection;
import de.verdox.vcore.data.datatypes.VCoreData;
import de.verdox.vcore.data.manager.VCoreDataManager;
import org.bson.Document;
import org.redisson.api.RMap;

import java.util.Set;
import java.util.UUID;

public abstract class DataSession <S extends VCoreData<S>> {

    protected final VCoreDataManager<S,?> dataManager;

    public DataSession(VCoreDataManager<S,?> dataManager){
        this.dataManager = dataManager;
        onLoad();
    }

    public S load(Class<? extends S> dataClass, UUID objectUUID){
        if(!dataExistRemote(dataClass,objectUUID))
            dataBaseToRedis(dataClass,objectUUID);
        if(!dataExistLocally(dataClass,objectUUID))
            loadFromRedis(dataClass,objectUUID);
        return getData(dataClass,objectUUID);
    }

    public void save(Class<? extends S> dataClass, UUID objectUUID){
        if(!dataExistRemote(dataClass,objectUUID))
            return;
        redisToDatabase(dataClass,objectUUID,getRedisKeys(dataClass,objectUUID));
        dataManager.pushRemoval(dataClass,objectUUID);
    }

    public final boolean dataExistRemote(Class<? extends S> dataClass, UUID uuid){
        RMap<String, Object> redisCache = getRedisCache(dataClass);

        Set<String> redisKeys = getRedisKeys(dataClass,uuid);

        return redisKeys.parallelStream().anyMatch(redisCache::containsKey);
    }

    public abstract void onLoad();
    public abstract void onCleanUp();

    public final void cleanUp(){
        onCleanUp();
    }

    public abstract RMap<String, Object> getRedisCache(Class<? extends S> dataClass);

    public abstract MongoCollection<Document> getMongoCollection(Class<? extends S> dataClass);

    public abstract void loadFromRedis (Class<? extends S> dataClass, UUID objectUUID);
    public abstract void pushToRedis (S dataObject, Class<? extends S> dataClass, UUID objectUUID);

    public abstract void dataBaseToRedis(Class<? extends S> dataClass, UUID objectUUID);
    public abstract void redisToDatabase(Class<? extends S> dataClass,UUID objectUUID, Set<String> dataKeysToSave);

    public abstract <T extends S> T getData (Class<? extends T> type, UUID uuid);

    public abstract boolean dataExistLocally(Class<? extends S> dataClass, UUID uuid);


    public abstract Set<String> getRedisKeys(Class<? extends S> vCoreDataClass, UUID uuid);

    public abstract void addData(S data, Class<? extends S> dataClass, boolean push);
    public abstract void removeData(Class<? extends S> dataClass, UUID uuid, boolean push);
}
