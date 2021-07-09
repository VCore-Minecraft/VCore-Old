/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.pingservice;

import com.google.common.eventbus.Subscribe;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.pingservice.events.ServerPingOfflineEvent;
import de.verdox.vcore.plugin.pingservice.events.ServerPingOnlineEvent;
import de.verdox.vcore.synchronization.messaging.event.MessageEvent;
import de.verdox.vcore.synchronization.messaging.messages.Message;
import de.verdox.vcore.synchronization.messaging.messages.MessageWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 09.07.2021 01:27
 */
public abstract class ServerPingManager<T extends VCorePlugin<?,?>> {

    protected final T vCorePlugin;
    protected final ServerCache serverCache;
    protected final ServerPingConfig serverPingConfig;

    public ServerPingManager(T vCorePlugin){
        this.vCorePlugin = vCorePlugin;
        this.serverCache = new ServerCache(this);
        this.serverPingConfig = new ServerPingConfig(vCorePlugin,"serverInfo.yml", "//settings");
        this.serverPingConfig.init();
        vCorePlugin.getServices().getVCoreScheduler().asyncInterval(this::sendOnlinePing,20L*60,20L*10);
    }

    public abstract void sendOnlinePing();
    public abstract void sendOfflinePing();

    public ServerCache getServerCache() {
        return serverCache;
    }

    public UUID getServerUUID(String serverName){
        return UUID.nameUUIDFromBytes(serverName.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.UTF_8));
    }

    public static class Bukkit extends ServerPingManager<VCorePlugin.Minecraft>{
        public Bukkit(VCorePlugin.Minecraft vCorePlugin) {
            super(vCorePlugin);
        }

        @Override
        public void sendOnlinePing() {
            if(serverPingConfig.isBungeeMode()){
                Message message = vCorePlugin.getServices().getMessagingService().constructMessage()
                        .withParameters("serverStatus","online")
                        .withData(ServerType.BUKKIT.name(),serverPingConfig.getServerName(),serverPingConfig.getServerAddress(),serverPingConfig.getServerPort())
                        .constructMessage();
                vCorePlugin.getServices().getMessagingService().publishMessage(message);
            }
        }

        @Override
        public void sendOfflinePing() {
            if(serverPingConfig.isBungeeMode()){
                Message message = vCorePlugin.getServices().getMessagingService().constructMessage()
                        .withParameters("serverStatus","offline")
                        .withData(ServerType.BUKKIT.name(),serverPingConfig.getServerName(),serverPingConfig.getServerAddress(),serverPingConfig.getServerPort())
                        .constructMessage();
                vCorePlugin.getServices().getMessagingService().publishMessage(message);
            }
        }
    }

    public static class BungeeCord extends ServerPingManager<VCorePlugin.BungeeCord>{
        public BungeeCord(VCorePlugin.BungeeCord vCorePlugin) {
            super(vCorePlugin);
        }

        @Override
        public void sendOnlinePing() {
            if(serverPingConfig.isBungeeMode()){
                Message message = vCorePlugin.getServices().getMessagingService().constructMessage()
                        .withParameters("serverStatus","online")
                        .withData(ServerType.PROXY.name(),serverPingConfig.getServerName(),serverPingConfig.getServerAddress(),serverPingConfig.getServerPort())
                        .constructMessage();
                vCorePlugin.getServices().getMessagingService().publishMessage(message);
            }
        }

        @Override
        public void sendOfflinePing() {
            if(serverPingConfig.isBungeeMode()){
                Message message = vCorePlugin.getServices().getMessagingService().constructMessage()
                        .withParameters("serverStatus","offline")
                        .withData(ServerType.PROXY.name(),serverPingConfig.getServerName(),serverPingConfig.getServerAddress(),serverPingConfig.getServerPort())
                        .constructMessage();
                vCorePlugin.getServices().getMessagingService().publishMessage(message);
            }
        }
    }

    public static class ServerCache {

        private final Map<UUID, ServerInstance> cachedServers = new ConcurrentHashMap<>();
        private final ServerPingManager<?> serverPingManager;

        ServerCache(ServerPingManager<?> serverPingManager){
            this.serverPingManager = serverPingManager;
            this.serverPingManager.vCorePlugin.getServices().eventBus.register(this);
        }

        @Subscribe
        void onServerPing(MessageEvent e){
            MessageWrapper messageWrapper = new MessageWrapper(e.getMessage());
            if(!messageWrapper.validate(String.class, String.class, String.class, Integer.class))
                return;
            if(messageWrapper.parameterContains("serverStatus")){

                String serverType = e.getMessage().getData(0,String.class);
                String serverName = e.getMessage().getData(1,String.class);
                String serverAddress = e.getMessage().getData(2,String.class);
                int serverPort = e.getMessage().getData(3,Integer.class);
                UUID serverUUID = serverPingManager.getServerUUID(serverName);

                if(messageWrapper.parameterContains("serverStatus","online")){
                    boolean firstReceived = false;
                    if(!cachedServers.containsKey(serverUUID)) {
                        cachedServers.put(serverUUID, new ServerInstance(ServerType.valueOf(serverType), serverName, serverAddress, serverPort));
                        firstReceived = true;
                    }
                    serverPingManager.vCorePlugin.getServices().eventBus.post(new ServerPingOnlineEvent(ServerType.valueOf(serverType), serverName, serverAddress, serverPort,firstReceived));
                }
                else if(messageWrapper.parameterContains("serverStatus","offline")){
                    serverPingManager.vCorePlugin.getServices().eventBus.post(new ServerPingOfflineEvent(ServerType.valueOf(serverType), serverName, serverAddress, serverPort));
                    if(cachedServers.containsKey(serverUUID))
                        cachedServers.remove(serverUUID);
                }
            }
        }
    }
}
