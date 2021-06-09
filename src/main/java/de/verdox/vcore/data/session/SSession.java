package de.verdox.vcore.data.session;

import de.verdox.vcore.data.annotations.DataContext;
import de.verdox.vcore.data.annotations.PreloadStrategy;
import de.verdox.vcore.data.datatypes.ServerData;
import de.verdox.vcore.data.manager.VCoreDataManager;
import de.verdox.vcore.data.session.datahandler.LocalDataHandler;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SSession extends DataSession<ServerData>{

    private final Map<Class<? extends ServerData>, Map<UUID,ServerData>> serverDataObjects;

    private final VCoreSubsystem<?> vCoreSubsystem;

    public SSession(VCoreDataManager<ServerData, ?> dataManager, VCoreSubsystem<?> vCoreSubsystem) {
        super(dataManager,vCoreSubsystem.getUuid());
        this.serverDataObjects = new ConcurrentHashMap<>();
        this.vCoreSubsystem = vCoreSubsystem;


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
                .forEach(this::loadAllDataFromDatabase);
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onCleanUp() {

    }

    @Override
    public String getMongoDBSuffix() {
        return "ServerData";
    }

    @Override
    public Set<ServerData> getAllData(Class<? extends ServerData> dataClass) {
        return new HashSet<>(serverDataObjects.get(dataClass).values());
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
    protected LocalDataHandler<ServerData> setupLocalDatabaseHandler() {
        return null;
    }

    @Override
    public <T extends ServerData> T getDataLocal(Class<? extends T> type, UUID uuid) {
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
}
