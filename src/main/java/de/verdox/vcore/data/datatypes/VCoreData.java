package de.verdox.vcore.data.datatypes;

import de.verdox.vcore.data.annotations.DataContext;
import de.verdox.vcore.data.annotations.VCorePersistentData;
import de.verdox.vcore.data.session.DataSession;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.redisson.RedisManager;
import de.verdox.vcore.redisson.VCorePersistentDatabaseData;
import de.verdox.vcore.redisson.VCoreRedisData;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import org.redisson.api.RTopic;
import org.redisson.api.listener.MessageListener;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public abstract class VCoreData implements VCoreRedisData, VCorePersistentDatabaseData {

    //TODO: Methode um alle ServerData UUIDs für eine Klasse aus Datenbank oder Cache zu laden

    public static Set<String> getRedisDataKeys(Class<? extends VCoreData> vCoreDataClass){
        return Arrays.stream(vCoreDataClass.getDeclaredFields())
                .filter(field -> field.getAnnotation(VCorePersistentData.class) != null)
                .map(Field::getName)
                .collect(Collectors.toSet());
    }

    private MessageListener<Map<String,Object>> messageListener;
    protected final RedisManager<?> redisManager;
    private final UUID objectUUID;
    private boolean cleaned = false;
    @VCorePersistentData
    protected long lastUse = 0;

    public VCoreData(RedisManager<?> redisManager, UUID objectUUID){
        this.redisManager = redisManager;
        this.objectUUID = objectUUID;
        RTopic topic = getDataTopic();
        if(topic == null)
            return;

        if(redisManager.getContext(getClass()).equals(DataContext.GLOBAL)) {
            this.messageListener = (channel, map) -> {
                restoreFromRedis(map);
                updateLastUse();
            };
            topic.addListener(Map.class,messageListener);
            updateLastUse();
        }
    }

    public final UUID getUUID() {
        updateLastUse();
        return objectUUID;
    }

    public final void cleanUp(){
        if(getDataTopic() == null)
            return;
        onCleanUp();
        getDataTopic().removeListener(messageListener);
        cleaned = true;
        updateLastUse();
    }

    public final RTopic getDataTopic(){
        return redisManager.getTopic(getClass(),getUUID());
    }

    //TODO: PushUpdate auslagern in die DataSession und Objekt als Eingabeparameter
    public final void pushUpdate(){
        getResponsibleDataSession().localToRedis(this,this.getClass(),getUUID());
    }

    public final Map<String, Object> dataForRedis() {
        if(cleaned)
            getPlugin().consoleMessage("Potential data leak at: " + getClass().getCanonicalName() + " as it has already been cleaned.",true);
        Map<String, Object> dataForRedis = new HashMap<>();
        updateLastUse();

        getRedisDataKeys(getClass()).forEach(dataKey -> {
            try {
                Field field = getClass().getField(dataKey);
                if(field.get(this) == null)
                    return;
                dataForRedis.put(field.getName(), field.get(this));
            } catch (NoSuchFieldException | IllegalAccessException e) { e.printStackTrace(); }
        });
        return dataForRedis;
    }

    //TODO Interessant für Lokale Server Daten die nicht über Redis synchronisiert werden müssen
    // Müssen in die Load Pipeline extra eingegliedert werden vermutlich?

    @Override
    public final void restoreFromDataBase(Map<String, Object> dataFromDatabase) {
        dataFromDatabase.forEach((key, value) -> {
            try { getClass().getField(key).set(this,value); } catch (IllegalAccessException | NoSuchFieldException e) { e.printStackTrace(); }
        });
    }

    @Override
    public final void restoreFromRedis(Map<String, Object> dataFromRedis) {
        dataFromRedis.forEach((key, value) -> {
            try { getClass().getField(key).set(this,value); } catch (IllegalAccessException | NoSuchFieldException ignored) { }
        });
    }

    public VCorePlugin<?,?> getPlugin(){return redisManager.getPlugin();}

    public final VCoreSubsystem<?> getRequiredSubsystem(){
        updateLastUse();
        return redisManager.getPlugin().getSubsystemManager().findSubsystemByClass(VCorePlugin.findDependSubsystemClass(getClass()));
    }

    protected void updateLastUse(){
        lastUse = System.currentTimeMillis();
    }
    public long getLastUse() {
        return lastUse;
    }

    public abstract void onLoad();
    public abstract void onCleanUp();
    public void debugToConsole(){
        dataForRedis().forEach((s, o) -> {
            getPlugin().consoleMessage("&e"+s+"&7: &b"+o.toString(),2,true);
        });
    }

    public abstract DataSession getResponsibleDataSession();


}
