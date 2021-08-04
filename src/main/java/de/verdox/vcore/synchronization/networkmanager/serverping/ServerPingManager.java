/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.serverping;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.networkmanager.NetworkManager;
import de.verdox.vcore.synchronization.networkmanager.serverping.files.ServerPingConfig;
import de.verdox.vcore.synchronization.messaging.messages.Message;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 09.07.2021 01:27
 */
public class ServerPingManager<T extends VCorePlugin<?,?>> {

    protected final ServerPingConfig serverPingConfig;
    private final NetworkManager<T> networkManager;

    public ServerPingManager(NetworkManager<T> networkManager){
        this.networkManager = networkManager;
        this.serverPingConfig = new ServerPingConfig(networkManager.getPlugin(),"serverInfo.yml", "//settings");
        this.serverPingConfig.init();
        //TODO: Muss aus sein, bevor Server schließt, sonst Ping falsch
        networkManager.getPlugin().getServices().getVCoreScheduler().asyncInterval(this::sendOnlinePing,20L*60,20L*10);
    }

    public void sendOnlinePing(){
        if(!serverPingConfig.isBungeeMode())
            return;
        Message message = networkManager.getPlugin().getServices().getMessagingService().constructMessage()
                .withParameters("serverStatus","online")
                .withData(networkManager.getServerType().name(),serverPingConfig.getServerName(),serverPingConfig.getServerAddress(),serverPingConfig.getServerPort())
                .constructMessage();
        networkManager.getPlugin().getServices().getMessagingService().publishMessage(message);
    }

    public void sendOfflinePing(){
        if(!serverPingConfig.isBungeeMode())
            return;
        Message message = networkManager.getPlugin().getServices().getMessagingService().constructMessage()
                .withParameters("serverStatus","offline")
                .withData(networkManager.getServerType().name(),serverPingConfig.getServerName(),serverPingConfig.getServerAddress(),serverPingConfig.getServerPort())
                .constructMessage();
        networkManager.getPlugin().getServices().getMessagingService().publishMessage(message);
    }

    public UUID getServerUUID(String serverName){
        return UUID.nameUUIDFromBytes(serverName.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.UTF_8));
    }

    public String getServerName(){
        return serverPingConfig.getServerName();
    }
}
