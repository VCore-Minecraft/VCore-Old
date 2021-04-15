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

    private final Map<VCoreSubsystem<?>, SSession> dataCache = new ConcurrentHashMap<>();

    public ServerDataManager(R plugin, boolean useRedisCluster, String[] addressArray, DataConnection.MongoDB mongoDB) {
        super(plugin, useRedisCluster, addressArray, mongoDB);
        plugin.getSubsystemManager().getActivatedSubSystems().forEach(vCoreSubsystem -> dataCache.put(vCoreSubsystem,new SSession(this,vCoreSubsystem)));
    }

    @Override
    protected void onCleanupInterval() {
        plugin.getSubsystemManager().getActiveServerDataClasses().forEach(aClass -> {
            getAllData(aClass).forEach(serverData -> {
                if(System.currentTimeMillis() - serverData.getLastUse() <= 1000L*1800)
                    return;
                // Wurde das Datum in den letzten 1800 Sekunden nicht genutzt wird es in Redis geladen
                serverData.getResponsibleDataManager().save(serverData.getClass(),serverData.getUUID());
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

    public SSession getDataHolder(VCoreSubsystem<?> subsystem){
        if(!dataCache.containsKey(subsystem))
            return null;
        return dataCache.get(subsystem);
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
        SSession serverDataSession = dataCache.get(subsystem);
        if(serverDataSession.dataExistLocally(dataClass,objectUUID))
            return serverDataSession.getData(dataClass,objectUUID);

        try {
            ServerData dataObject =  dataClass.getDeclaredConstructor(ServerDataManager.class,UUID.class).newInstance(this,objectUUID);
            serverDataSession.addData(dataObject,dataClass,true);
            return dataClass.cast(dataObject);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<ServerData> getAllData(Class<? extends ServerData> dataClass) {
        Set<ServerData> dataSet = new HashSet<>();
        getPlugin()
                .getSubsystemManager()
                .getSubSystems()
                .forEach(vCoreSubsystem -> dataSet.addAll(getDataHolder(vCoreSubsystem).getAllData(dataClass)));
        return dataSet;
    }

}
