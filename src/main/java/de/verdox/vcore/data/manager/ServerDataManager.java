package de.verdox.vcore.data.manager;

import de.verdox.vcore.data.datatypes.ServerData;
import de.verdox.vcore.data.session.DataSession;
import de.verdox.vcore.data.session.SSession;
import de.verdox.vcore.dataconnection.DataConnection;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.redisson.events.RedisMessageEvent;
import de.verdox.vcore.redisson.messages.RedisSimpleMessage;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import org.redisson.api.RTopic;
import org.redisson.api.listener.MessageListener;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ServerDataManager <R extends VCorePlugin<?,?>> extends VCoreDataManager<ServerData, R> {

    private final UUID sessionUUID = UUID.randomUUID();
    private final Map<UUID, SSession> dataCache = new ConcurrentHashMap<>();
    private final RTopic messenger;
    //TODO: Maybe change to RedisMessage
    private final MessageListener<RedisSimpleMessage> channelListener;

    public ServerDataManager(R plugin, boolean useRedisCluster, String[] addressArray, String redisPassword, DataConnection.MongoDB mongoDB) {
        super(plugin, useRedisCluster, addressArray, redisPassword, mongoDB);
        plugin.consoleMessage("&eStarting ServerDataManager&7...",false);
        plugin
                .getSubsystemManager()
                .getActivatedSubSystems()
                .forEach(vCoreSubsystem -> {
                    plugin.consoleMessage("&eStarting DataSession for &7: &b"+vCoreSubsystem, false);
                    SSession sSession = new SSession(this, vCoreSubsystem);
                    dataCache.put(vCoreSubsystem.getUuid(), sSession);
                    sSession.preloadData();
                });
        messenger = getRedisManager().getRedissonClient().getTopic("VCoreDataManager:Messenger:"+getClass().getName());
        channelListener = (channel, msg) -> {
            if(!msg.getPluginName().equals(getPlugin().getPluginName()))
                return;
            plugin.consoleMessage("&eReceived a message from: "+msg.getSenderUUID()+" &8[&b"+msg.getPluginName()+"&8]",true);
            System.out.println(msg);
            plugin.getEventBus().post(new RedisMessageEvent(sessionUUID, msg));
        };
        messenger.addListener(RedisSimpleMessage.class,channelListener);
        loaded = true;
    }

    public void broadcastRedisMessage(RedisSimpleMessage.Builder builder){
        long timeItTook = messenger.publish(builder.constructSimpleMessage(plugin.getPluginName(), getSessionUUID()));
        plugin.consoleMessage("&eMessage sent in &6"+timeItTook+"ms &8[&c"+plugin.getPluginName()+"&8]", true);
    }

    @Override
    public void saveAllData() {
        getAllSessions().forEach(SSession::saveAllData);
    }

    @Override
    public void shutDown() {
        saveAllData();
    }

    @Override
    public <T extends ServerData> T load(@Nonnull Class<? extends T> type, @Nonnull UUID uuid, @Nonnull LoadingStrategy loadingStrategy, boolean createIfNotExist, Consumer<T> callback) {
        VCoreSubsystem<?> subsystem = plugin.findDependSubsystem(type);
        if(subsystem == null)
            throw new NullPointerException("Subsystem "+subsystem.getClass()+" could not be found in plugin"+plugin.getPluginName());
        DataSession<ServerData> dataSession = getSession(subsystem.getUuid());
        if(dataSession == null) {
            if (loadingStrategy.equals(LoadingStrategy.LOAD_LOCAL))
                throw new IllegalStateException("Subsystem " + subsystem.getClass().getSimpleName() + " does not have a datasession in " + plugin.getPluginName());
            else
                dataSession = createSession(subsystem.getUuid());
        }
        if(!loadingStrategy.equals(LoadingStrategy.LOAD_PIPELINE)) {
            if(!getSession(subsystem.getUuid()).getLocalDataHandler().dataExistLocally(type, uuid)){
                if(loadingStrategy.equals(LoadingStrategy.LOAD_LOCAL))
                    throw new NullPointerException(type+" with uuid "+uuid+" does not exist in local cache of subsystem "+subsystem);
                else{
                    DataSession<ServerData> finalDataSession = dataSession;
                    plugin.async(() -> {
                        T data = finalDataSession.loadFromPipeline(type, uuid, createIfNotExist);
                        if(callback != null)
                            callback.accept(data);
                    });
                }
            }
            if(!dataSession.getLocalDataHandler().dataExistLocally(type, uuid))
                return null;
            return dataSession.getLocalDataHandler().getDataLocal(type,uuid);
        }
        return dataSession.loadFromPipeline(type, uuid, createIfNotExist);
    }

    //TODO: Push and Save to database
    @Override
    protected void onCleanupInterval() {
        plugin.getSubsystemManager().getActiveServerDataClasses().forEach(aClass -> {
            getAllData(aClass).forEach(serverData -> {
                if(System.currentTimeMillis() - serverData.getLastUse() <= 1000L*1800)
                    return;
                // Wurde das Datum in den letzten 1800 Sekunden nicht genutzt wird es von Redis in die Datenbank geschrieben und aus Redis entfernt.
                serverData.getResponsibleDataSession().saveAndRemoveLocally(serverData.getClass(),serverData.getUUID());
            });
        });
    }

    @Override
    protected DataSession<ServerData> createSession(@Nonnull UUID objectUUID) {
        VCoreSubsystem<?> subsystem = plugin.getSubsystemManager().getActivatedSubSystems().stream().filter(vCoreSubsystem -> vCoreSubsystem.getUuid().equals(objectUUID)).findFirst().orElse(null);
        if(subsystem == null)
            return null;
        SSession sSession = new SSession(this, subsystem);
        dataCache.put(objectUUID, sSession);
        sSession.preloadData();
        return sSession;
    }

    @Override
    protected boolean exist(@Nonnull UUID objectUUID) {
        return dataCache.containsKey(objectUUID);
    }

    @Override
    protected DataSession<ServerData> deleteSession(@Nonnull UUID objectUUID) {
        return dataCache.remove(objectUUID);
    }

    @Override
    public DataSession<ServerData> getSession(@Nonnull UUID objectUUID) {
        if(!dataCache.containsKey(objectUUID))
            throw new NullPointerException("No ServerDataSession for UUID: "+objectUUID);
        return dataCache.get(objectUUID);
    }

    @Override
    public ServerData instantiateVCoreData(@Nonnull Class<? extends ServerData> dataClass, UUID objectUUID) {
        if(dataClass == null)
            throw new NullPointerException("dataClass can't be null!");
        if(VCorePlugin.findDependSubsystemClass(dataClass) == null)
            throw new NullPointerException(dataClass+" does not have RequiredSubsystem Annotation set.");
        if(objectUUID == null)
            throw new NullPointerException("objectUUID can't be null!");
        VCoreSubsystem<?> subsystem = plugin.findDependSubsystem(dataClass);
        if(subsystem == null)
            throw new NullPointerException("Provided Subsystem can't be null");
        if(!subsystem.isActivated())
            throw new NullPointerException("Provided Subsystem is not activated");
        SSession serverDataSession = dataCache.get(subsystem.getUuid());
        if(serverDataSession == null)
            throw new NullPointerException("Subsystem "+subsystem+" does not have a datasession in "+plugin.getPluginName());
        if(serverDataSession.getLocalDataHandler().dataExistLocally(dataClass,objectUUID))
            return serverDataSession.getLocalDataHandler().getDataLocal(dataClass,objectUUID);

        try {
            ServerData dataObject =  dataClass.getDeclaredConstructor(ServerDataManager.class,UUID.class).newInstance(this,objectUUID);
            return dataClass.cast(dataObject);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public <U extends ServerData> Set<U> getAllData(@Nonnull Class<? extends U> dataClass) {
        Set<U> dataSet = new HashSet<>();
        getPlugin()
                .getSubsystemManager()
                .getSubSystems()
                .forEach(vCoreSubsystem -> dataSet.addAll(getSession(vCoreSubsystem.getUuid()).getLocalDataHandler().getAllLocalData(dataClass)));
        return dataSet;
    }

    public SSession getDataSession(VCoreSubsystem<?> subsystem){
        return (SSession) getSession(subsystem.getUuid());
    }

    public Set<SSession> getAllSessions(){
        return new HashSet<>(dataCache.values());
    }

    public UUID getSessionUUID() {
        return sessionUUID;
    }

}
