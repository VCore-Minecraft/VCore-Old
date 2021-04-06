package de.verdox.vcore.data.datatypes;

import de.verdox.vcore.data.annotations.VCorePersistentData;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.redisson.RedisManager;
import de.verdox.vcore.redisson.VCorePersistentDatabaseData;
import de.verdox.vcore.redisson.VCoreRedisData;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import org.redisson.api.RTopic;
import org.redisson.api.listener.MessageListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class VCoreData <S extends VCoreData<S>> implements VCoreRedisData, VCorePersistentDatabaseData {

    //TODO: Methode um alle ServerData UUIDs für eine Klasse aus Datenbank oder Cache zu laden

    private MessageListener<Map<String,Object>> messageListener;
    protected final RedisManager<?> redisManager;
    private final UUID objectUUID;
    private boolean cleaned = false;


    public VCoreData(RedisManager<?> redisManager, UUID objectUUID){
        this.redisManager = redisManager;
        this.objectUUID = objectUUID;
        RTopic topic = getDataTopic();
        if(topic == null)
            return;

       this.messageListener = (channel, map) -> {
            restoreFromRedis(map);
        };
        topic.addListener(Map.class,messageListener);
    }

    public UUID getUUID() {
        return objectUUID;
    }

    public final void cleanUp(){
        if(getDataTopic() == null)
            return;
        onCleanUp();
        getDataTopic().removeListener(messageListener);
        cleaned = true;
    }

    public abstract RTopic getDataTopic();
    public abstract void pushUpdate();

    public final Map<String, Object> dataForRedis() {
        if(cleaned)
            System.out.println("Potential data leak at: "+getClass().getCanonicalName()+" as it has already been cleaned.");
        Map<String, Object> dataForRedis = new HashMap<>();

        Arrays.stream(getClass().getDeclaredFields())
                .filter(field -> field.getAnnotation(VCorePersistentData.class) != null)
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        if(field.get(this) == null)
                            return;
                        dataForRedis.put(VCorePlugin.getMongoDBIdentifier(this.getClass())+":"+field.getName(), field.get(this));
                    } catch (IllegalAccessException e) { e.printStackTrace(); }
                });
        return dataForRedis;
    }

    public VCoreSubsystem<?> getRequiredSubsystem(){
        return redisManager.getPlugin().getSubsystemManager().findSubsystemByClass(VCorePlugin.findDependSubsystemClass(getClass()));
    }

    public abstract void onLoad();
    public abstract void onCleanUp();
}
