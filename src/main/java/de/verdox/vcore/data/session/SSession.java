package de.verdox.vcore.data.session;

import com.mongodb.client.MongoCollection;
import de.verdox.vcore.data.annotations.RequiredSubsystemInfo;
import de.verdox.vcore.data.annotations.VCorePersistentData;
import de.verdox.vcore.data.datatypes.ServerData;
import de.verdox.vcore.data.manager.VCoreDataManager;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import org.bson.Document;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SSession extends DataSession<ServerData>{

    private final Map<Class<? extends ServerData>, RMap<String,Object>> dataCaches = new ConcurrentHashMap<>();
    private final Map<Class<? extends ServerData>, Map<UUID,ServerData>> serverDataObjects;

    private final VCoreSubsystem<?> vCoreSubsystem;

    public SSession(VCoreDataManager<ServerData, ?> dataManager, VCoreSubsystem<?> vCoreSubsystem) {
        super(dataManager);
        this.serverDataObjects = new ConcurrentHashMap<>();
        this.vCoreSubsystem = vCoreSubsystem;
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onCleanUp() {

    }

    @Override
    public RMap<String, Object> getRedisCache(Class<? extends ServerData> dataClass) {
        if(!dataCaches.containsKey(dataClass))
            dataCaches.put(dataClass,dataManager.getRedisManager().getRedissonClient()
                    .getMap("VCoreServerData:"+VCorePlugin.getMongoDBIdentifier(vCoreSubsystem.getClass())+":"+VCorePlugin.getMongoDBIdentifier(dataClass)));
        return dataCaches.get(dataClass);
    }

    @Override
    public MongoCollection<Document> getMongoCollection(Class<? extends ServerData> dataClass) {
        Class<? extends VCoreSubsystem<?>> subsystemClass = VCorePlugin.findDependSubsystemClass(dataClass);
        if(subsystemClass == null)
            throw new NullPointerException("Dependent Subsystem Annotation not set. ["+dataClass.getCanonicalName()+"]");
        String mongoIdentifier = VCorePlugin.getMongoDBIdentifier(subsystemClass);
        if(mongoIdentifier == null)
            throw new NullPointerException("MongoDBIdentifier Annotation not set. ["+subsystemClass.getCanonicalName()+"]");
        return dataManager.getRedisManager().getMongoDB().getCollection(VCorePlugin.getMongoDBIdentifier(subsystemClass));
    }

    @Override
    public void loadFromRedis(Class<? extends ServerData> dataClass, UUID objectUUID) {
        if(dataClass == null)
            return;
        if(objectUUID == null)
            return;
        RMap<String, Object> redisCache = getRedisCache(dataClass);
        ServerData serverData = dataManager.instantiateVCoreData(dataClass,objectUUID);

        Set<String> redisKeys = getRedisKeys(dataClass,objectUUID);
        Map<String, Object> dataFromRedis = new HashMap<>();

        // TODO: Evtl nicht auf [2] setzen
        redisKeys.forEach(key -> dataFromRedis.put(key.split(":")[2],redisCache.get(key)));

        serverData.restoreFromRedis(dataFromRedis);

        addData(serverData,dataClass,true);
    }

    @Override
    public void pushToRedis(ServerData dataObject, Class<? extends ServerData> dataClass, UUID objectUUID) {
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

    @Override
    public void dataBaseToRedis(Class<? extends ServerData> dataClass, UUID objectUUID) {
        if(dataClass == null)
            return;
        if(objectUUID == null)
            return;

        ServerData serverData = dataManager.instantiateVCoreData(dataClass,objectUUID);

        //TODO: Evtl wieder in playerUUID umbenennen
        Document mongoDBData = getMongoCollection(dataClass).find(new Document("objectUUID",objectUUID.toString())).first();

        if(mongoDBData == null)
            mongoDBData = new Document("objectUUID", objectUUID.toString());

        Map<String, Object> dataFromDatabase = new HashMap<>();

        mongoDBData.forEach((key, data) -> {
            if(!key.contains(":"))
                return;
            String[]split = key.split(":");
            if(!split[0].equals(VCorePlugin.getMongoDBIdentifier(dataClass)))
                return;
            // TODO: Evtl nicht auf [2] setzen
            dataFromDatabase.put(key.split(":")[2],data);
        });
        serverData.dataForRedis().forEach(getRedisCache(dataClass)::put);

        serverData.restoreFromDataBase(dataFromDatabase);
    }

    @Override
    public void redisToDatabase(Class<? extends ServerData> dataClass, UUID objectUUID, Set<String> dataKeysToSave) {
        if(dataClass == null)
            return;
        if(objectUUID == null)
            return;
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

    @Override
    public <T extends ServerData> T getData(Class<? extends T> type, UUID uuid) {
        if(!dataExistLocally(type,uuid))
            return null;
        return type.cast(serverDataObjects.get(type).get(uuid));
    }

    @Override
    public boolean dataExistLocally(Class<? extends ServerData> dataClass, UUID uuid) {
        if(!serverDataObjects.containsKey(dataClass))
            return false;
        return serverDataObjects.get(dataClass).containsKey(uuid);
    }

    @Override
    public Set<String> getRedisKeys(Class<? extends ServerData> vCoreDataClass, UUID uuid) {
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

    @Override
    public void addData(ServerData data, Class<? extends ServerData> dataClass, boolean push) {
        if(dataExistLocally(dataClass,data.getUUID()))
            return;
        if(!serverDataObjects.containsKey(dataClass))
            serverDataObjects.put(dataClass,new ConcurrentHashMap<>());
        //TODO: Hier das Erstellen von neuen Daten reinpushen
        serverDataObjects.get(dataClass).put(data.getUUID(),data);

        if(push)
            dataManager.pushCreation(dataClass,data);
    }

    @Override
    public void removeData(Class<? extends ServerData> dataClass, UUID uuid, boolean push) {
        if(!dataExistLocally(dataClass,uuid))
            return;
        serverDataObjects.get(dataClass).remove(uuid);
        if(serverDataObjects.get(dataClass).size() == 0)
            serverDataObjects.remove(dataClass);
        //TODO: Hier das Löschen von Daten reinpushen

        if(push)
            dataManager.pushRemoval(dataClass,uuid);
    }
}
