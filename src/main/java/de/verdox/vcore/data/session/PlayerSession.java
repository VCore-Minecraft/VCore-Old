package de.verdox.vcore.data.session;

import de.verdox.vcore.data.datatypes.PlayerData;
import de.verdox.vcore.data.manager.VCoreDataManager;

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
    public void onLoad() {}

    @Override
    public Set<PlayerData> getAllData(Class<? extends PlayerData> dataClass) {
        return playerDataObjects.values().parallelStream().filter(playerData -> playerData.getClass().equals(dataClass)).collect(Collectors.toSet());
    }

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
    public void addData(PlayerData data, Class<? extends PlayerData> dataClass, boolean push) {
        playerDataObjects.put(dataClass,data);

        if(push)
            dataManager.pushCreation(dataClass,data);
    }

    @Override
    public void removeData(Class<? extends PlayerData> dataClass, UUID uuid, boolean push) {
        playerDataObjects.remove(dataClass);
        if(push)
            dataManager.pushRemoval(dataClass,getUuid());
    }

    @Override
    public void onCleanUp() {
        playerDataObjects.forEach((aClass, playerData) -> playerData.cleanUp());
    }

    @Override
    public String getMongoDBSuffix() {
        return "PlayerData";
    }
}
