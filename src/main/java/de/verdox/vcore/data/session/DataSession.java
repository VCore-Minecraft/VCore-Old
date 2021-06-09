package de.verdox.vcore.data.session;

import com.mongodb.client.MongoCollection;
import de.verdox.vcore.data.annotations.DataContext;
import de.verdox.vcore.data.annotations.RequiredSubsystemInfo;
import de.verdox.vcore.data.datatypes.VCoreData;
import de.verdox.vcore.data.manager.VCoreDataManager;
import de.verdox.vcore.data.session.datahandler.LocalDataHandler;
import org.bson.Document;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;

import java.util.*;

public abstract class DataSession <S extends VCoreData> {

    protected final VCoreDataManager<S,?> dataManager;
    private UUID uuid;
    private final LocalDataHandler<S> localDataHandler;

    public DataSession(VCoreDataManager<S,?> dataManager, UUID uuid){
        this.dataManager = dataManager;
        this.uuid = uuid;
        this.localDataHandler = setupLocalDatabaseHandler();
        onLoad();
    }

    public final UUID getUuid() {
        return uuid;
    }

    public final void cleanUp(){
        onCleanUp();
    }

    public S loadFromPipeline(Class<? extends S> dataClass, UUID objectUUID){
        if(dataClass == null || objectUUID == null)
            return null;

        if(dataExistLocally(dataClass,objectUUID)) {
            dataManager.getPlugin().consoleMessage("&eFound Data in Local Cache &8[&b"+dataClass.getSimpleName()+"&8]", 1,true);
        }
        else if(dataExistRedis(dataClass,objectUUID)) {
            dataManager.getPlugin().consoleMessage("&eFound Data in Redis Cache &8[&b"+dataClass.getSimpleName()+"&8]", 1,true);
            redisToLocal(dataClass, objectUUID);
        }
        else if(dataExistInDatabase(dataClass,objectUUID)) {
            dataManager.getPlugin().consoleMessage("&eFound Data in Database &8[&b"+dataClass.getSimpleName()+"&8]", 1,true);
            if(dataManager.getRedisManager().getContext(dataClass).equals(DataContext.GLOBAL)){
                dataBaseToRedis(dataClass, objectUUID);
                redisToLocal(dataClass,objectUUID);
            }
            else {
                databaseToLocal(dataClass,objectUUID);
            }
        }
        else {
            dataManager.getPlugin().consoleMessage("&eNo Data was found. Creating new data! &8[&b"+dataClass.getSimpleName()+"&8]", 1,true);
            S vCoreData = dataManager.instantiateVCoreData(dataClass,objectUUID);
            addDataLocally(vCoreData,dataClass,true);
            localToRedis(vCoreData,dataClass,vCoreData.getUUID());
        }
        dataManager.getPlugin().consoleMessage("&eLoaded &a"+dataClass.getSimpleName()+" &ewith uuid&7: "+objectUUID, 1,true);
        return getDataLocal(dataClass,objectUUID);
    }

    private boolean dataExistInDatabase(Class<? extends S> dataClass, UUID objectUUID){
        Document document = getMongoCollection(dataClass).find(new Document("_id",objectUUID.toString())).first();
        return document != null;
    }

    public void saveToPipeline(Class<? extends S> dataClass, UUID objectUUID){
        if(dataManager.getRedisManager().getContext(dataClass).equals(DataContext.GLOBAL)){
            if(!dataExistRedis(dataClass,objectUUID))
                //TODO: Es wird hier nur in die Datenbank gespeichert, wenn remote die Daten noch existieren,
                // um zu verhindern, dass lokal noch Reste bestehen die nicht existieren sollten
                return;
            redisToDatabase(dataClass,objectUUID);
        }
        else {
            localToDatabase(dataClass,objectUUID);
        }
    }

    public void saveAndRemoveLocally(Class<? extends S> dataClass, UUID objectUUID){
        saveToPipeline(dataClass,objectUUID);
        removeDataLocally(dataClass,objectUUID,true);
    }


    //TODO: First search in Redis then search in MongoDB
    protected void loadAllDataFromDatabase(Class<? extends S> dataClass){
        dataManager.getPlugin().consoleMessage("&ePreloading Data for &a"+dataClass.getSimpleName()+" &efrom database&7!",true);
        getMongoCollection(dataClass)
                .find()
                .iterator()
                .forEachRemaining(document -> {
                    UUID uuid = UUID.fromString(document.getString("_id"));
                    loadFromPipeline(dataClass,uuid);
                });
    }

    public final boolean dataExistRedis(Class<? extends S> dataClass, UUID uuid){
        RMap<String, Object> redisCache = getRedisCache(dataClass);

        Set<String> redisKeys = getRedisKeys(dataClass,uuid);

        return redisKeys.parallelStream().anyMatch(redisCache::containsKey);
    }

    public final void localToRedis(S dataObject, Class<? extends S> dataClass, UUID objectUUID){
        if(dataClass == null)
            return;
        if(objectUUID == null)
            return;
        // If it is a Local Datum it wont be published to redis again.
        // Method should not be invoked if local anyway
        if(dataManager.getRedisManager().getContext(dataClass).equals(DataContext.LOCAL))
            return;

        RMap<String, Object> redisCache = getRedisCache(dataClass);

        dataObject.dataForRedis().forEach(redisCache::put);
        RTopic topic = dataObject.getDataTopic();

        long start = System.currentTimeMillis();
        topic.publishAsync(dataObject.dataForRedis());
        long end = System.currentTimeMillis() - start;
        dataManager.getPlugin().consoleMessage("&ePushing Update&7: &b"+objectUUID+" &7[&e"+end+" ms&7]",true);
    }

    public final void localToDatabase(Class<? extends S> dataClass,UUID objectUUID){
        saveToDatabase(dataClass,objectUUID, getDataLocal(dataClass,objectUUID).dataForRedis());
    }

    public final void dataBaseToRedis(Class<? extends S> dataClass, UUID objectUUID){
        S vCoreData = dataManager.instantiateVCoreData(dataClass,objectUUID);
        RMap<String, Object> redisCache = getRedisCache(dataClass);

        Map<String, Object> dataFromDatabase = loadDataFromDatabase(dataClass,objectUUID);

        vCoreData.dataForRedis().forEach(redisCache::put);
        vCoreData.restoreFromRedis(dataFromDatabase);
    }

    /**
     * Method to save from Database to Local
     * @param dataClass
     * @param objectUUID
     */
    public final void databaseToLocal(Class<? extends S> dataClass, UUID objectUUID){
        S vCoreData = dataManager.instantiateVCoreData(dataClass,objectUUID);

        Map<String, Object> dataFromDatabase = loadDataFromDatabase(dataClass,objectUUID);
        vCoreData.restoreFromDataBase(dataFromDatabase);
    }

    /**
     * Method to save Data from Redis to Local Cache
     * @param dataClass
     * @param objectUUID
     */
    public final void redisToLocal(Class<? extends S> dataClass, UUID objectUUID){

        if(dataClass == null)
            return;
        if(objectUUID == null)
            return;
        RMap<String, Object> redisCache = getRedisCache(dataClass);
        S vCoreData = dataManager.instantiateVCoreData(dataClass,objectUUID);

        Set<String> redisKeys = getRedisKeys(dataClass,objectUUID);
        Map<String, Object> dataFromRedis = new HashMap<>();

        redisKeys.forEach(key -> dataFromRedis.put(key,redisCache.get(key)));

        vCoreData.restoreFromRedis(dataFromRedis);

        addDataLocally(vCoreData,dataClass,true);
    }

    /**
     * Method to save Data from Redis to Database
     * @param dataClass
     * @param objectUUID
     */

    public final void redisToDatabase(Class<? extends S> dataClass,UUID objectUUID){
        if(dataClass == null)
            return;
        RMap<String, Object> redisCache = getRedisCache(dataClass);
        saveToDatabase(dataClass,objectUUID,redisCache);
    }

    /**
     * Method to directly save some Data to MongoDB Database
     * @param dataClass
     * @param objectUUID
     * @param dataToSave
     */

    protected final void saveToDatabase(Class<? extends S> dataClass, UUID objectUUID, Map<String, Object> dataToSave){
        if(dataClass == null)
            return;
        if(objectUUID == null)
            return;
        if(dataToSave == null)
            return;
        if(dataToSave.isEmpty())
            return;
        Set<String> dataKeysToSave = getRedisKeys(dataClass,objectUUID);
        if(dataKeysToSave == null)
            return;
        getDataLocal(dataClass,objectUUID).cleanUp();

        dataKeysToSave
                .stream()
                .filter(s -> !dataToSave.containsKey(s))
                .forEach(dataToSave::remove);

        MongoCollection<Document> mongoCollection = getMongoCollection(dataClass);

        Document filter = new Document("_id",objectUUID.toString());

        if(mongoCollection.find(filter).first() == null){
            Document newData = new Document("_id",objectUUID.toString());
            newData.putAll(dataToSave);
            mongoCollection.insertOne(newData);
        }
        else {
            Document newData = new Document(dataToSave);
            Document updateFunc = new Document("$set",newData);
            mongoCollection.updateOne(filter,updateFunc);
        }
    }

    private Map<String, Object> loadDataFromDatabase(Class<? extends S> dataClass, UUID objectUUID){

        Document filter = new Document("objectUUID",objectUUID.toString());

        Document mongoDBData = getMongoCollection(dataClass).find(filter).first();

        if(mongoDBData == null)
            mongoDBData = filter;
        Map<String, Object> dataFromDatabase = new HashMap<>();
        mongoDBData.forEach(dataFromDatabase::put);
        return dataFromDatabase;
    }

    public final Set<String> getRedisKeys(Class<? extends S> vCoreDataClass, UUID uuid){
        RequiredSubsystemInfo requiredSubsystemInfo = vCoreDataClass.getAnnotation(RequiredSubsystemInfo.class);
        if(requiredSubsystemInfo == null)
            throw new RuntimeException(getClass().getSimpleName()+" does not have RequiredSubsystemInfo Annotation set");
        if(uuid == null)
            return new HashSet<>();
        if(VCoreData.getRedisDataKeys(vCoreDataClass) == null)
            throw new NullPointerException(VCoreData.class.getSimpleName()+" does not provide RedisDataKeys");
        return VCoreData.getRedisDataKeys(vCoreDataClass);
    }

    public final RMap<String, Object> getRedisCache(Class<? extends S> dataClass){
        return dataManager.getRedisManager().getRedisCache(dataClass,getUuid());
    }

    public final MongoCollection<Document> getMongoCollection(Class<? extends S> dataClass){
        return dataManager.getRedisManager().getMongoDB().getDataProvider(dataClass,getMongoDBSuffix());
    }
    public abstract String getMongoDBSuffix();

    public abstract void onLoad();
    public abstract void onCleanUp();

    public abstract boolean dataExistLocally(Class<? extends S> dataClass, UUID uuid);
    public abstract <T extends S> T getDataLocal(Class<? extends T> type, UUID uuid);
    public abstract void addDataLocally(S data, Class<? extends S> dataClass, boolean push);
    public abstract void removeDataLocally(Class<? extends S> dataClass, UUID uuid, boolean push);
    public abstract Set<S> getAllData(Class<? extends S> dataClass);
    public abstract void debugToConsole();

    public final LocalDataHandler<S> getLocalDataHandler() {
        return localDataHandler;
    }

    protected abstract LocalDataHandler<S> setupLocalDatabaseHandler();
}
