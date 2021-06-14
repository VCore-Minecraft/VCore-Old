package de.verdox.vcore.data.session;

import de.verdox.vcore.data.annotations.PreloadStrategy;
import de.verdox.vcore.data.datatypes.PlayerData;
import de.verdox.vcore.data.manager.VCoreDataManager;
import de.verdox.vcore.data.session.datahandler.local.LocalDataHandler;
import de.verdox.vcore.plugin.VCorePlugin;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerSession extends DataSession<PlayerData>{

    private final Map<Class<? extends PlayerData>, PlayerData> playerDataObjects = new HashMap<>();
    private final VCoreDataManager<PlayerData, ?> dataManager;

    public PlayerSession(VCoreDataManager<PlayerData, ?> dataManager, UUID playerUUID) {
        super(dataManager, playerUUID);
        this.dataManager = dataManager;
    }

    @Override
    public void preloadData() {
        dataManager
                .getPlugin()
                .getSubsystemManager()
                .getActivePlayerDataClasses()
                .stream()
                // Load Data on Server Start
                .filter(aClass -> dataManager.getRedisManager().getPreloadStrategy(aClass).equals(PreloadStrategy.LOAD_BEFORE))
                // PreLoad every Server Data (First from Redis, if it does not exist in redis, load from database)
                .forEach(this::loadAllDataFromDatabaseToPipeline);
    }

    @Override
    public void onLoad() {}

    public Map<Class<? extends PlayerData>, PlayerData> getPlayerDataObjects() {
        return playerDataObjects;
    }

    @Override
    public void debugToConsole() {
        dataManager.getPlugin().consoleMessage("&8--- &6Debugging PlayerSession&7: &b"+getUuid()+" &8---",false);
        playerDataObjects.forEach((aClass, playerData) -> {
            dataManager.getPlugin().consoleMessage("",false);
            dataManager.getPlugin().consoleMessage("&b"+aClass.getCanonicalName()+"&7: ",1,false);
            playerData.debugToConsole();
        });
        dataManager.getPlugin().consoleMessage("&8---\t\t\t\t\t\t\t\t&8---",false);
    }

    @Override
    public LocalDataHandler<PlayerData> setupLocalHandler() {
        return new LocalDataHandler<>(this) {
            @Override
            public boolean dataExistLocally(Class<? extends PlayerData> dataClass, UUID uuid) {
                return playerDataObjects.containsKey(dataClass);
            }

            @Override
            public void addDataLocally(PlayerData data, Class<? extends PlayerData> dataClass, boolean push) {
                playerDataObjects.put(dataClass,data);
                if(push)
                    dataManager.pushCreation(dataClass,data);
            }

            @Override
            public void removeDataLocally(Class<? extends PlayerData> dataClass, UUID uuid, boolean push) {
                playerDataObjects.remove(dataClass);
                if(push)
                    dataManager.pushRemoval(dataClass,getUuid());
            }

            @Override
            public <T extends PlayerData> T getDataLocal(Class<? extends T> type, UUID uuid) {
                if(!playerDataObjects.containsKey(type))
                    throw new NullPointerException("No data in local cache with type "+type+" and uuid "+uuid);
                return type.cast(playerDataObjects.get(type));
            }

            @Override
            public <T extends PlayerData> Set<T> getAllLocalData(Class<? extends T> dataClass) {
                return playerDataObjects.values()
                        .parallelStream()
                        .filter(playerData -> playerData.getClass().equals(dataClass))
                        .map(dataClass::cast)
                        .collect(Collectors.toSet());
            }

            @Override
            public Set<Object> getCachedKeys() {
                return Collections.singleton(playerDataObjects.keySet());
            }
        };
    }


    @Override
    public void onCleanUp() {
        playerDataObjects.forEach((aClass, playerData) -> playerData.cleanUp());
    }

    @Override
    public void saveAllData() {
        playerDataObjects.forEach((aClass, playerData) -> playerData.pushUpdate());
    }

    @Override
    public String getMongoDBSuffix() {
        return "PlayerData";
    }
}
