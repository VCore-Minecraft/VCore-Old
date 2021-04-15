package de.verdox.vcore.data.session;

import com.mongodb.client.MongoCollection;
import de.verdox.vcore.data.annotations.RequiredSubsystemInfo;
import de.verdox.vcore.data.annotations.VCorePersistentData;
import de.verdox.vcore.data.datatypes.VCoreData;
import de.verdox.vcore.data.manager.VCoreDataManager;
import de.verdox.vcore.plugin.VCorePlugin;
import org.bson.Document;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;

import java.util.*;
import java.util.stream.Collectors;

public abstract class DataSession <S extends VCoreData> {

    protected final VCoreDataManager<S,?> dataManager;

    public DataSession(VCoreDataManager<S,?> dataManager){
        this.dataManager = dataManager;
        onLoad();
    }

    public final void cleanUp(){
        onCleanUp();
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
        redisToDatabase(dataClass,objectUUID);
        dataManager.pushRemoval(dataClass,objectUUID);
    }

    public final boolean dataExistRemote(Class<? extends S> dataClass, UUID uuid){
        RMap<String, Object> redisCache = getRedisCache(dataClass);

        Set<String> redisKeys = getRedisKeys(dataClass,uuid);

        return redisKeys.parallelStream().anyMatch(redisCache::containsKey);
    }

    public final void pushToRedis (S dataObject, Class<? extends S> dataClass, UUID objectUUID){
        if(dataClass == null)
            return;
        if(objectUUID == null)
            return;
        RMap<String, Object> redisCache = getRedisCache(dataClass);

        dataObject.dataForRedis().forEach(redisCache::put);
        RTopic topic = dataObject.getDataTopic();

        long start = System.currentTimeMillis();
        topic.publishAsync(dataObject.dataForRedis());
        long end = System.currentTimeMillis() - start;
        dataManager.getPlugin().consoleMessage("&ePushing Update&7: &b"+objectUUID+" &7[&e"+end+" ms&7]");
    }

    public final void dataBaseToRedis(Class<? extends S> dataClass, UUID objectUUID){

        S vCoreData = dataManager.instantiateVCoreData(dataClass,objectUUID);
        RMap<String, Object> redisCache = getRedisCache(dataClass);

        Document mongoDBData = getMongoCollection(dataClass).find(new Document("objectUUID",objectUUID.toString())).first();

        if(mongoDBData == null)
            mongoDBData = new Document("objectUUID", objectUUID.toString());

        Map<String, Object> dataFromDatabase = new HashMap<>();

        //TODO: Für SPIELERDATEN
        //        mongoDBData.forEach((key, data) -> {
        //            if(!key.contains(":"))
        //                return;
        //            String[]split = key.split(":");
        //            if(!split[0].equals(VCorePlugin.getMongoDBIdentifier(dataClass)))
        //                return;
        //            dataFromDatabase.put(key.split(":")[1],data);
        //        });

        //TODO: Für Spielerdaten und Serverdaten einheitlich machen!

        //TODO: Für SERVERDATEN

        mongoDBData.forEach((key, data) -> {
            if(!key.contains(":"))
                return;
            String[]split = key.split(":");
            if(!split[0].equals(VCorePlugin.getMongoDBIdentifier(dataClass)))
                return;
            // TODO: Evtl nicht auf [2] setzen
            dataFromDatabase.put(key.split(":")[2],data);
        });

        vCoreData.dataForRedis().forEach(redisCache::put);
        vCoreData.restoreFromDataBase(dataFromDatabase);
    }

    public final void redisToDatabase(Class<? extends S> dataClass,UUID objectUUID){
        if(dataClass == null)
            return;
        if(objectUUID == null)
            return;
        Set<String> dataKeysToSave = getRedisKeys(dataClass,objectUUID);
        if(dataKeysToSave == null)
            return;
        getData(dataClass,objectUUID).cleanUp();
        RMap<String, Object> redisCache = getRedisCache(dataClass);
        dataKeysToSave
                .parallelStream()
                .filter(redisCache::containsKey)
                .forEach(dataKey -> {
                    Document serverData = new Document("objectUUID",objectUUID.toString());
                    serverData.putAll(redisCache);
                    getMongoCollection(dataClass).insertOne(serverData);
                    redisCache.remove(dataKey);
                });
    }

    public final void loadFromRedis (Class<? extends S> dataClass, UUID objectUUID){

        if(dataClass == null)
            return;
        if(objectUUID == null)
            return;
        RMap<String, Object> redisCache = getRedisCache(dataClass);
        S vCoreData = dataManager.instantiateVCoreData(dataClass,objectUUID);

        Set<String> redisKeys = getRedisKeys(dataClass,objectUUID);
        Map<String, Object> dataFromRedis = new HashMap<>();

        redisKeys.forEach(key -> dataFromRedis.put(key.split(":")[2],redisCache.get(key)));

        vCoreData.restoreFromRedis(dataFromRedis);

        addData(vCoreData,dataClass,true);
    }

    public final Set<String> getRedisKeys(Class<? extends S> vCoreDataClass, UUID uuid){
        RequiredSubsystemInfo requiredSubsystemInfo = vCoreDataClass.getAnnotation(RequiredSubsystemInfo.class);
        if(requiredSubsystemInfo == null)
            throw new RuntimeException(getClass().getSimpleName()+" does not have RequiredSubsystemInfo Annotation set");
        if(uuid == null)
            return new HashSet<>();

        return Arrays.stream(vCoreDataClass.getDeclaredFields())
                .filter(field -> field.getAnnotation(VCorePersistentData.class) != null)
                .map(field -> VCorePlugin.getMongoDBIdentifier(vCoreDataClass)+":"+uuid.toString()+":"+field.getName())
                .collect(Collectors.toSet());
    }

    public final RMap<String, Object> getRedisCache(Class<? extends S> dataClass){
        return dataManager.getRedisManager().getRedisCache(dataClass,getUUID());
    }

    public final MongoCollection<Document> getMongoCollection(Class<? extends S> dataClass){
        return dataManager.getRedisManager().getMongoDB().getCollection(dataClass,getMongoDBSuffix());
    }

    public abstract UUID getUUID();
    public abstract String getMongoDBSuffix();

    public abstract void onLoad();
    public abstract void onCleanUp();

    public abstract boolean dataExistLocally(Class<? extends S> dataClass, UUID uuid);
    public abstract <T extends S> T getData (Class<? extends T> type, UUID uuid);
    public abstract void addData(S data, Class<? extends S> dataClass, boolean push);
    public abstract void removeData(Class<? extends S> dataClass, UUID uuid, boolean push);
    public abstract Set<S> getAllData(Class<? extends S> dataClass);
}
