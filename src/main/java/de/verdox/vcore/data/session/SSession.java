package de.verdox.vcore.data.session;

import de.verdox.vcore.data.annotations.PreloadStrategy;
import de.verdox.vcore.data.datatypes.ServerData;
import de.verdox.vcore.data.manager.VCoreDataManager;
import de.verdox.vcore.data.session.datahandler.local.LocalDataHandler;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SSession extends DataSession<ServerData>{

    private final Map<Class<? extends ServerData>, Map<UUID,ServerData>> serverDataObjects;

    private final VCoreSubsystem<?> vCoreSubsystem;

    public SSession(VCoreDataManager<ServerData, ?> dataManager, VCoreSubsystem<?> vCoreSubsystem) {
        super(dataManager,vCoreSubsystem.getUuid());
        this.serverDataObjects = new ConcurrentHashMap<>();
        this.vCoreSubsystem = vCoreSubsystem;
    }

    @Override
    public void preloadData() {
        dataManager
                .getPlugin()
                .getSubsystemManager()
                .getActiveServerDataClasses()
                .stream()
                // Find all Server Data Classes that belong to this session
                .filter(aClass -> VCorePlugin.findDependSubsystemClass(aClass).equals(this.vCoreSubsystem.getClass()))
                // Load Data on Server Start
                .filter(aClass -> dataManager.getRedisManager().getPreloadStrategy(aClass).equals(PreloadStrategy.LOAD_BEFORE))
                // PreLoad every Server Data (First from Redis, if it does not exist in redis, load from database)
                //TODO: Wenn Daten bisher nicht in die Datenbank gesaved wurden werden sie NICHT preLoaded -> Das muss man fixen
                .forEach(dataClass -> {
                    loadAllFromRedis(dataClass);
                    loadAllFromDatabase(dataClass);
                });
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onCleanUp() {

    }

    @Override
    public void saveAllData() {
        serverDataObjects.forEach((aClass, uuidServerDataMap) -> {
            uuidServerDataMap.forEach((uuid, serverData) -> {
                serverData.pushUpdate(true);
            });
        });
    }

    @Override
    public String getMongoDBSuffix() {
        return "ServerData";
    }

    @Override
    public void debugToConsole() {
        dataManager.getPlugin().consoleMessage("&8--- &6Debugging ServerDataSession&7: &b"+getUuid()+" &8---",false);
        serverDataObjects.forEach((aClass, serverDataMap) -> {
            dataManager.getPlugin().consoleMessage("&b"+aClass.getCanonicalName()+"&7: ",1,false);
            serverDataMap.forEach((uuid, serverData) -> {
                dataManager.getPlugin().consoleMessage("&b"+aClass.getCanonicalName()+"&7: ",2,false);
            });
        });
        dataManager.getPlugin().consoleMessage("&8---\t\t\t\t\t\t\t\t\t&8---",false);
    }

    @Override
    protected LocalDataHandler<ServerData> setupLocalHandler() {
        return new LocalDataHandler<>(this) {
            @Override
            public boolean dataExistLocally(Class<? extends ServerData> dataClass, UUID uuid) {
                if(!serverDataObjects.containsKey(dataClass))
                    return false;
                return serverDataObjects.get(dataClass).containsKey(uuid);
            }

            @Override
            public void addDataLocally(ServerData data, Class<? extends ServerData> dataClass, boolean push) {
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
            public void removeDataLocally(Class<? extends ServerData> dataClass, UUID uuid, boolean push) {
                if(!dataExistLocally(dataClass,uuid))
                    return;
                serverDataObjects.get(dataClass).remove(uuid);
                if(serverDataObjects.get(dataClass).size() == 0)
                    serverDataObjects.remove(dataClass);
                //TODO: Hier das Löschen von Daten reinpushen

                if(push)
                    dataManager.pushRemoval(dataClass,uuid);
            }

            @Override
            public <T extends ServerData> T getDataLocal(Class<? extends T> type, UUID uuid) {
                if(!dataExistLocally(type,uuid))
                    throw new NullPointerException("No data in local cache with type "+type+" and uuid "+uuid);
                return type.cast(serverDataObjects.get(type).get(uuid));
            }

            @Override
            public <T extends ServerData> Set<T> getAllLocalData(Class<? extends T> dataClass) {
                if(!serverDataObjects.containsKey(dataClass))
                    return new HashSet<>();
                return serverDataObjects.get(dataClass).values()
                        .parallelStream()
                        .filter(serverData -> serverData.getClass().equals(dataClass))
                        .map(dataClass::cast)
                        .collect(Collectors.toSet());
            }

            @Override
            public Set<Object> getCachedKeys() {
                return Collections.singleton(serverDataObjects.values());
            }
        };
    }

    public VCoreSubsystem<?> getVCoreSubsystem() {
        return vCoreSubsystem;
    }
}
