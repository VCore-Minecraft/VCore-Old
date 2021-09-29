/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.serverping;

import de.verdox.vcore.plugin.SystemLoadable;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.messaging.messages.Message;
import de.verdox.vcore.synchronization.networkmanager.NetworkManager;
import de.verdox.vcore.synchronization.networkmanager.serverping.files.ServerPingConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 09.07.2021 01:27
 */
public class ServerPingManager<T extends VCorePlugin<?, ?>> implements SystemLoadable {

    protected final ServerPingConfig serverPingConfig;
    private final NetworkManager<T> networkManager;
    private final ScheduledFuture<?> keepAlivePing;
    AtomicBoolean loaded = new AtomicBoolean();

    public ServerPingManager(@NotNull NetworkManager<T> networkManager) {
        Objects.requireNonNull(networkManager, "networkManager can't be null!");
        this.networkManager = networkManager;
        this.serverPingConfig = new ServerPingConfig(networkManager.getPlugin(), "serverInfo.yml", "//settings");
        this.serverPingConfig.init();
        loaded.set(true);
        keepAlivePing = networkManager.getPlugin().getServices().getVCoreScheduler().asyncInterval(() -> {
            if (loaded.get())
                sendOnlinePing();
            else
                sendOfflinePing();
        }, 20L, 20L * 5);
    }

    public void sendOnlinePing() {
        if (!serverPingConfig.isBungeeMode())
            return;
        Message message = networkManager.getPlugin().getServices().getMessagingService().constructMessage()
                .withParameters("serverStatus", "online")
                .withData(networkManager.getServerType().name(), serverPingConfig.getServerName(), serverPingConfig.getServerAddress(), serverPingConfig.getServerPort())
                .constructMessage();
        networkManager.getPlugin().getServices().getMessagingService().publishMessage(message);
    }

    public void sendOfflinePing() {
        if (!serverPingConfig.isBungeeMode())
            return;
        Message message = networkManager.getPlugin().getServices().getMessagingService().constructMessage()
                .withParameters("serverStatus", "offline")
                .withData(networkManager.getServerType().name(), serverPingConfig.getServerName(), serverPingConfig.getServerAddress(), serverPingConfig.getServerPort())
                .constructMessage();
        networkManager.getPlugin().getServices().getMessagingService().publishMessage(message);
    }

    public String getServerName() {
        return serverPingConfig.getServerName();
    }

    @Override
    public boolean isLoaded() {
        return loaded.get();
    }

    @Override
    public void shutdown() {
        loaded.set(false);
        //TODO: Klappt manchmal nicht so ganz
        keepAlivePing.cancel(false);
        sendOfflinePing();
    }
}
