package de.verdox.vcore.data.session;

import de.verdox.vcore.data.datatypes.PlayerData;
import de.verdox.vcore.data.manager.VCoreDataManager;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerSession extends DataSession<PlayerData>{

    private final Map<Class<? extends PlayerData>, PlayerData> playerDataObjects = new HashMap<>();
    private final VCoreDataManager<PlayerData, ?> dataManager;
    private final UUID playerUUID;

    public PlayerSession(VCoreDataManager<PlayerData, ?> dataManager, UUID playerUUID) {
        super(dataManager);
        this.dataManager = dataManager;
        this.playerUUID = playerUUID;
    }

    @Override
    public void onLoad() {
        System.out.println("PlayerSession loaded for player with uuid: "+getUUID());
    }

    @Override
    public Set<PlayerData> getAllData(Class<? extends PlayerData> dataClass) {
        return playerDataObjects.values().parallelStream().filter(playerData -> playerData.getClass().equals(dataClass)).collect(Collectors.toSet());
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
        if(push)
            dataManager.pushRemoval(dataClass,playerUUID);
    }

    @Override
    public void onCleanUp() {
        playerDataObjects.forEach((aClass, playerData) -> playerData.cleanUp());
    }

    @Override
    public UUID getUUID() {
        return playerUUID;
    }

    @Override
    public String getMongoDBSuffix() {
        return "PlayerData";
    }
}
