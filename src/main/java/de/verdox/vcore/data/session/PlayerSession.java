package de.verdox.vcore.data.session;

import com.mongodb.client.MongoCollection;
import de.verdox.vcore.data.datatypes.PlayerData;
import de.verdox.vcore.data.manager.VCoreDataManager;
import de.verdox.vcore.data.annotations.RequiredSubsystemInfo;
import de.verdox.vcore.data.annotations.VCorePersistentData;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.redisson.RedisManager;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import org.bson.Document;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PlayerSession extends DataSession<PlayerData>{

    private final Map<VCoreSubsystem<?>, RMap<String,Object>> subsystemCaches = new ConcurrentHashMap<>();
    private final Map<Class<? extends PlayerData>, PlayerData> playerDataObjects = new HashMap<>();
    private final VCoreDataManager<PlayerData, ?> dataManager;
    private final UUID playerUUID;

    public PlayerSession(VCoreDataManager<PlayerData, ?> dataManager, UUID playerUUID) {
        super(dataManager);
        this.dataManager = dataManager;
        this.playerUUID = playerUUID;

        dataManager.getPlugin().getSubsystemManager().getSubSystems().forEach(vCoreSubsystem -> {
            RedisManager<?> redisManager = dataManager.getRedisManager();
            RMap<String,Object> subsystemCache = redisManager.getRedissonClient().getMap(redisManager.generateSubsystemKey(vCoreSubsystem,playerUUID));
            subsystemCaches.put(vCoreSubsystem,subsystemCache);
        });
    }

    @Override
    public void onLoad() {
        System.out.println("PlayerSession loaded for player with uuid: "+getPlayerUUID());
    }

    @Override
    public RMap<String, Object> getRedisCache(Class<? extends PlayerData> dataClass) {
        VCoreSubsystem<?> subsystem = dataManager.getPlugin().getSubsystemManager().findSubsystemByClass(VCorePlugin.findDependSubsystemClass(dataClass));

        if(!this.subsystemCaches.containsKey(subsystem))
            return null;
        return subsystemCaches.get(subsystem);
    }

    @Override
    public MongoCollection<Document> getMongoCollection(Class<? extends PlayerData> dataClass) {
        Class<? extends VCoreSubsystem<?>> subsystemClass = VCorePlugin.findDependSubsystemClass(dataClass);
        if(subsystemClass == null)
            throw new NullPointerException("Dependent Subsystem Annotation not set. ["+dataClass.getCanonicalName()+"]");
        String mongoIdentifier = VCorePlugin.getMongoDBIdentifier(subsystemClass);
        if(mongoIdentifier == null)
            throw new NullPointerException("MongoDBIdentifier Annotation not set. ["+subsystemClass.getCanonicalName()+"]");
        return dataManager.getRedisManager().getMongoDB().getCollection(VCorePlugin.getMongoDBIdentifier(subsystemClass));
    }

    @Override
    public void loadFromRedis(Class<? extends PlayerData> dataClass, UUID objectUUID) {
        RMap<String, Object> redisCache = getRedisCache(dataClass);
        PlayerData playerData = dataManager.instantiateVCoreData(dataClass,objectUUID);

        Set<String> redisKeys = getRedisKeys(dataClass,objectUUID);

        Map<String, Object> dataFromRedis = new HashMap<>();

        redisKeys.forEach(key -> dataFromRedis.put(key.split(":")[1],redisCache.get(key)));

        playerData.restoreFromRedis(dataFromRedis);
        playerData.onLoad();

        addData(playerData,dataClass,true);
    }

    @Override
    public void pushToRedis(PlayerData dataObject, Class<? extends PlayerData> dataClass, UUID objectUUID) {
        RMap<String, Object> redisCache = getRedisCache(dataClass);

        dataObject.dataForRedis().forEach(redisCache::put);
        RTopic topic = dataObject.getDataTopic();

        long start = System.currentTimeMillis();
        topic.publishAsync(dataObject.dataForRedis());
        long end = System.currentTimeMillis() - start;
        dataManager.getPlugin().consoleMessage("&ePushing Update&7: &b"+objectUUID+" &7[&e"+end+" ms&7]");
    }

    @Override
    public void dataBaseToRedis(Class<? extends PlayerData> dataClass, UUID objectUUID) {
        PlayerData playerData = dataManager.instantiateVCoreData(dataClass,objectUUID);

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
            dataFromDatabase.put(key.split(":")[1],data);
        });

        playerData.restoreFromDataBase(dataFromDatabase);

        playerData.dataForRedis().forEach(getRedisCache(dataClass)::put);
    }

    @Override
    public void redisToDatabase(Class<? extends PlayerData> dataClass, UUID objectUUID, Set<String> dataKeysToSave) {
        RMap<String, Object> redisCache = getRedisCache(dataClass);
        dataKeysToSave
                .parallelStream()
                .filter(redisCache::containsKey)
                .forEach(dataKey -> {
                    Document playerData = new Document("objectUUID",objectUUID.toString());
                    playerData.putAll(redisCache);
                    getMongoCollection(dataClass).insertOne(playerData);
                    redisCache.remove(dataKey);
                });
    }

    @Override
    public <T extends PlayerData> T getData(Class<? extends T> type, UUID uuid) {
        if(!playerDataObjects.containsKey(type))
            return null;
        return type.cast(playerDataObjects.get(type));
    }

    @Override
    public boolean dataExistLocally(Class<? extends PlayerData> dataClass, UUID uuid) {
        return playerDataObjects.containsKey(dataClass);
    }

    @Override
    public Set<String> getRedisKeys(Class<? extends PlayerData> vCoreDataClass, UUID uuid) {
        RequiredSubsystemInfo requiredSubsystemInfo = vCoreDataClass.getAnnotation(RequiredSubsystemInfo.class);
        if(requiredSubsystemInfo == null)
            throw new RuntimeException(getClass().getSimpleName()+" does not have RequiredSubsystemInfo Annotation set");

        return Arrays.stream(vCoreDataClass.getDeclaredFields())
                .filter(field -> field.getAnnotation(VCorePersistentData.class) != null)
                .map(field -> VCorePlugin.getMongoDBIdentifier(vCoreDataClass)+":"+field.getName())
                .collect(Collectors.toSet());
    }

    @Override
    public void addData(PlayerData data, Class<? extends PlayerData> dataClass, boolean push) {
        playerDataObjects.put(dataClass,data);

        if(push)
            dataManager.pushCreation(dataClass,data);
    }

    @Override
    public void removeData(Class<? extends PlayerData> dataClass, UUID uuid, boolean push) {
        if(push)
            dataManager.pushRemoval(dataClass,playerUUID);
    }

    @Override
    public void onCleanUp() {
        playerDataObjects.forEach((aClass, playerData) -> playerData.cleanUp());
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }
}
