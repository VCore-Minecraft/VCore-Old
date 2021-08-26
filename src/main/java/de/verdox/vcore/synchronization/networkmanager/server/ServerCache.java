/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.server;

import com.google.common.eventbus.Subscribe;
import de.verdox.vcore.synchronization.messaging.event.MessageEvent;
import de.verdox.vcore.synchronization.messaging.messages.MessageWrapper;
import de.verdox.vcore.synchronization.networkmanager.NetworkManager;
import de.verdox.vcore.synchronization.networkmanager.serverping.ServerPingManager;
import de.verdox.vcore.synchronization.networkmanager.serverping.events.ServerPingOfflineEvent;
import de.verdox.vcore.synchronization.networkmanager.serverping.events.ServerPingOnlineEvent;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 31.07.2021 20:59
 */
public class ServerCache {

    private final ServerPingManager<?> serverPingManager;
    private final NetworkManager<?> networkManager;

    public ServerCache(NetworkManager<?> networkManager) {
        this.serverPingManager = networkManager.getServerPingManager();
        this.networkManager = networkManager;
        networkManager.getPlugin().getServices().eventBus.register(this);
    }

    public UUID getServerUUID(String serverName) {
        return UUID.nameUUIDFromBytes(serverName.toUpperCase(Locale.ROOT).getBytes(StandardCharsets.UTF_8));
    }

    public boolean isServerNameTaken(String serverName) {
        return networkManager.getPlugin().getServices().getPipeline().exist(ServerInstance.class, getServerUUID(serverName), Pipeline.QueryStrategy.LOCAL, Pipeline.QueryStrategy.GLOBAL_CACHE);
    }

    @Subscribe
    void onServerPing(MessageEvent e) {
        MessageWrapper messageWrapper = new MessageWrapper(e.getMessage());
        if (!messageWrapper.validate(String.class, String.class, String.class, Integer.class))
            return;
        if (messageWrapper.parameterContains("serverStatus")) {

            String serverType = e.getMessage().getData(0, String.class);
            String serverName = e.getMessage().getData(1, String.class);
            String serverAddress = e.getMessage().getData(2, String.class);
            int serverPort = e.getMessage().getData(3, Integer.class);

            UUID serverUUID = getServerUUID(serverName);

            if (messageWrapper.parameterContains("serverStatus", "online")) {
                boolean firstReceived = false;
                if (!networkManager.getPlugin().getServices().getPipeline().exist(ServerInstance.class, serverUUID, Pipeline.QueryStrategy.LOCAL)) {
                    ServerInstance serverInstance = networkManager.getPlugin().getServices().getPipeline().load(ServerInstance.class, serverUUID, Pipeline.LoadingStrategy.LOAD_PIPELINE, true);

                    serverInstance.setServerName(serverName);
                    serverInstance.serverAddress = serverAddress;
                    serverInstance.serverPort = serverPort;
                    serverInstance.setServerType(ServerType.valueOf(serverType));
                    serverInstance.save(false);
                    firstReceived = true;
                }
                networkManager.getPlugin().getServices().eventBus.post(new ServerPingOnlineEvent(ServerType.valueOf(serverType), serverName, serverAddress, serverPort, firstReceived));
            } else if (messageWrapper.parameterContains("serverStatus", "offline")) {
                networkManager.getPlugin().getServices().eventBus.post(new ServerPingOfflineEvent(ServerType.valueOf(serverType), serverName, serverAddress, serverPort));
                networkManager.getPlugin().getServices().getPipeline().delete(ServerInstance.class, serverUUID);
            }
        }
    }
}
