/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager;

import de.verdox.vcore.plugin.SystemLoadable;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.networkmanager.server.ServerInstance;
import de.verdox.vcore.synchronization.networkmanager.player.VCorePlayer;
import de.verdox.vcore.synchronization.networkmanager.player.VCorePlayerCache;
import de.verdox.vcore.synchronization.networkmanager.server.ServerCache;
import de.verdox.vcore.synchronization.networkmanager.serverping.ServerPingManager;
import de.verdox.vcore.synchronization.networkmanager.server.ServerType;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 31.07.2021 21:01
 */
public class NetworkManager <T extends VCorePlugin<?,?>> implements SystemLoadable {

    private final ServerType serverType;
    private final T plugin;
    private final ServerPingManager<T> serverPingManager;
    private final ServerCache serverCache;
    private final VCorePlayerCache vCorePlayerCache;
    private boolean loaded;

    public NetworkManager(@Nonnull ServerType serverType,  @Nonnull T plugin){
        plugin.consoleMessage("&eStarting Network Manager&7!",false);
        this.serverType = serverType;
        this.plugin = plugin;
        this.serverPingManager = new ServerPingManager<>(this);
        this.serverCache = new ServerCache(this);
        this.vCorePlayerCache = new VCorePlayerCache(this);
        preloadData();
        loaded = true;
    }

    private void preloadData(){
        plugin.consoleMessage("&ePreloading Network Data&7...",false);
        getPlugin().getServices().getPipeline().loadAllData(VCorePlayer.class, Pipeline.LoadingStrategy.LOAD_PIPELINE);
        getPlugin().getServices().getPipeline().loadAllData(ServerInstance.class, Pipeline.LoadingStrategy.LOAD_PIPELINE);
        plugin.consoleMessage("&aPreloaded Network Data",false);
    }

    public VCorePlayer getVCorePlayer(UUID playerUUID){
        return plugin.getServices().getPipeline().load(VCorePlayer.class,playerUUID, Pipeline.LoadingStrategy.LOAD_PIPELINE,false);
    }

    public boolean isPlayerOnNetwork(UUID playerUUID){
        return plugin.getServices().getPipeline().exist(VCorePlayer.class,playerUUID, Pipeline.QueryStrategy.LOCAL, Pipeline.QueryStrategy.GLOBAL_CACHE);
    }

    public ServerInstance findPlayerServer(UUID playerUUID){
        VCorePlayer vCorePlayer = getVCorePlayer(playerUUID);
        if(vCorePlayer == null)
            return null;
        return getServer(vCorePlayer.currentGameServer);
    }

    public Map<String, List<VCorePlayer>> getAllPlayers(){
        Set<VCorePlayer> players = plugin.getServices().getPipeline().loadAllData(VCorePlayer.class, Pipeline.LoadingStrategy.LOAD_PIPELINE);

        Map<String, List<VCorePlayer>> playersByGameServers = players.stream().collect(Collectors.groupingBy(VCorePlayer::getCurrentGameServer));
        Map<String, List<VCorePlayer>> playersByProxyServers = players.stream().collect(Collectors.groupingBy(VCorePlayer::getCurrentProxyServer));
        playersByGameServers.putAll(playersByProxyServers);
        return playersByGameServers;
    }

    public Map<String, List<VCorePlayer>> getGameServerPlayers(){
        Set<VCorePlayer> players = plugin.getServices().getPipeline().loadAllData(VCorePlayer.class, Pipeline.LoadingStrategy.LOAD_PIPELINE);
        return players.stream().collect(Collectors.groupingBy(VCorePlayer::getCurrentGameServer));
    }

    public Map<String, List<VCorePlayer>> getProxyPlayers(){
        Set<VCorePlayer> players = plugin.getServices().getPipeline().loadAllData(VCorePlayer.class, Pipeline.LoadingStrategy.LOAD_PIPELINE);
        return players.stream().collect(Collectors.groupingBy(VCorePlayer::getCurrentProxyServer));
    }

    public ServerInstance getServer(String serverName){
        UUID serverUUID = UUID.nameUUIDFromBytes(serverName.getBytes(StandardCharsets.UTF_8));
        return plugin.getServices().getPipeline().load(ServerInstance.class,serverUUID, Pipeline.LoadingStrategy.LOAD_PIPELINE,false);
    }

    public ServerPingManager<T> getServerPingManager() {
        return serverPingManager;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public T getPlugin() {
        return plugin;
    }

    public ServerCache getServerCache() {
        return serverCache;
    }

    public VCorePlayerCache getVCorePlayerCache() {
        return vCorePlayerCache;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void shutdown() {
        serverPingManager.shutdown();
    }
}
