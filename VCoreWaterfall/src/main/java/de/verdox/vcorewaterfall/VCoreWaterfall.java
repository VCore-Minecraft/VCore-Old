/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorewaterfall;

import de.verdox.vcore.plugin.VCoreCoreInstance;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import de.verdox.vcore.synchronization.networkmanager.NetworkManager;
import de.verdox.vcore.synchronization.networkmanager.player.VCorePlayer;
import de.verdox.vcore.synchronization.networkmanager.player.api.VCorePlayerAPI;
import de.verdox.vcore.synchronization.networkmanager.player.api.bungeecord.VCorePlayerBungeeImpl;
import de.verdox.vcore.synchronization.networkmanager.player.listener.PlayerProxyListener;
import de.verdox.vcore.synchronization.networkmanager.server.ServerType;
import de.verdox.vcore.synchronization.networkmanager.server.api.VCoreServerAPI;
import de.verdox.vcore.synchronization.networkmanager.server.api.VCoreServerAPIImpl;
import de.verdox.vcorewaterfall.pings.ServerPingListener;
import net.md_5.bungee.api.ProxyServer;

import java.util.List;

public class VCoreWaterfall extends VCoreCoreInstance.BungeeCord {
    private NetworkManager<VCoreWaterfall> networkManager;
    private VCorePlayerAPI vCorePlayerAPI;
    private VCoreServerAPI vCoreServerAPI;

    //TODO: BungeeCord Fallback Server umgehen? Selber irgendwie fallback server bei join event bestimmen oder sowas

    @Override
    public void onPluginEnable() {
        networkManager = new NetworkManager<>(ServerType.PROXY, this);
        new PlayerProxyListener(networkManager);

        getServices().eventBus.register(new ServerPingListener());
        this.vCorePlayerAPI = new VCorePlayerBungeeImpl(this);
        this.vCoreServerAPI = new VCoreServerAPIImpl(this);

        if (networkManager.getServerCache().isServerNameTaken(getServerName())) {
            consoleMessage("&4<> ============================================= <>", false);
            consoleMessage("", false);
            consoleMessage("&cThe Server &e" + getServerName() + " &cis already online in this VCore Network &7(&bExists in Global Cache&7)", false);
            consoleMessage("&cShutting down other server&7!", false);
            consoleMessage("", false);
            consoleMessage("&4<> ============================================= <>", false);
            vCoreServerAPI.remoteShutdown(getServerName(), true);
        }
        networkManager.getServerPingManager().sendOnlinePing();
    }

    @Override
    public void onPluginDisable() {
        ProxyServer.getInstance().getPlayers().forEach(proxiedPlayer -> getServices().getPipeline().delete(VCorePlayer.class, proxiedPlayer.getUniqueId()));
        networkManager.shutdown();
    }

    @Override
    public List<VCoreSubsystem.BungeeCord> provideSubsystems() {
        return null;
    }

    @Override
    public VCorePlayerAPI getPlayerAPI() {
        return vCorePlayerAPI;
    }

    @Override
    public VCoreServerAPI getServerAPI() {
        return vCoreServerAPI;
    }

    @Override
    public String getServerName() {
        return networkManager.getServerPingManager().getServerName();
    }

    @Override
    public NetworkManager<VCoreWaterfall> getNetworkManager() {
        return networkManager;
    }
}
