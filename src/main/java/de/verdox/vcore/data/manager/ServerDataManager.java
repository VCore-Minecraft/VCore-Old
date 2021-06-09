package de.verdox.vcore.data.manager;

import de.verdox.vcore.data.datatypes.ServerData;
import de.verdox.vcore.data.session.DataSession;
import de.verdox.vcore.data.session.SSession;
import de.verdox.vcore.dataconnection.DataConnection;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServerDataManager <R extends VCorePlugin<?,?>> extends VCoreDataManager<ServerData, R> {

    private final Map<UUID, SSession> dataCache = new ConcurrentHashMap<>();

    public ServerDataManager(R plugin, boolean useRedisCluster, String[] addressArray, String redisPassword, DataConnection.MongoDB mongoDB) {
        super(plugin, useRedisCluster, addressArray, redisPassword, mongoDB);
        plugin
                .getSubsystemManager()
                .getActivatedSubSystems()
                .forEach(vCoreSubsystem -> dataCache.put(vCoreSubsystem.getUuid(), new SSession(this,vCoreSubsystem)));
    }

    @Override
    public ServerData load(Class<? extends ServerData> type, UUID uuid) {
        VCoreSubsystem<?> subsystem = plugin.findDependSubsystem(type);
        if(subsystem == null)
            return null;
        return getSession(subsystem.getUuid()).loadFromPipeline(type,uuid);
    }

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
    protected DataSession<ServerData> createSession(UUID objectUUID) {
        return null;
    }

    @Override
    protected boolean exist(UUID objectUUID) {
        return false;
    }

    @Override
    protected DataSession<ServerData> deleteSession(UUID objectUUID) {
        return null;
    }

    @Override
    public DataSession<ServerData> getSession(UUID objectUUID) {
        if(!dataCache.containsKey(objectUUID))
            return null;
        return dataCache.get(objectUUID);
    }

    @Override
    public ServerData instantiateVCoreData(Class<? extends ServerData> dataClass, UUID objectUUID) {
        if(VCorePlugin.findDependSubsystemClass(dataClass) == null)
            throw new NullPointerException(dataClass+" does not have RequiredSubsystem Annotation set.");
        if(objectUUID == null)
            return null;
        VCoreSubsystem<?> subsystem = plugin.findDependSubsystem(dataClass);
        if(subsystem == null)
            return null;
        if(!subsystem.isActivated())
            return null;
        SSession serverDataSession = dataCache.get(subsystem.getUuid());
        if(serverDataSession.dataExistLocally(dataClass,objectUUID))
            return serverDataSession.getDataLocal(dataClass,objectUUID);

        try {
            ServerData dataObject =  dataClass.getDeclaredConstructor(ServerDataManager.class,UUID.class).newInstance(this,objectUUID);
            return dataClass.cast(dataObject);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SSession getDataSession(VCoreSubsystem<?> subsystem){
        return (SSession) getSession(subsystem.getUuid());
    }

    @Override
    public Set<ServerData> getAllData(Class<? extends ServerData> dataClass) {
        Set<ServerData> dataSet = new HashSet<>();
        getPlugin()
                .getSubsystemManager()
                .getSubSystems()
                .forEach(vCoreSubsystem -> dataSet.addAll(getSession(vCoreSubsystem.getUuid()).getAllData(dataClass)));
        return dataSet;
    }

}
