package de.verdox.vcorewaterfall;

import de.verdox.vcore.plugin.VCoreCoreInstance;
import de.verdox.vcore.synchronization.networkmanager.NetworkManager;
import de.verdox.vcore.synchronization.networkmanager.player.api.VCorePlayerAPI;
import de.verdox.vcore.synchronization.networkmanager.player.api.bungeecord.VCorePlayerBungeeImpl;
import de.verdox.vcore.synchronization.networkmanager.server.ServerType;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import de.verdox.vcore.synchronization.networkmanager.player.listener.PlayerProxyListener;
import de.verdox.vcorewaterfall.pings.ServerPingListener;

import java.util.List;

public class VCoreWaterfall extends VCoreCoreInstance.BungeeCord {
    private NetworkManager<VCoreWaterfall> networkManager;
    private VCorePlayerAPI vCorePlayerAPI;

    //TODO: BungeeCord Fallback Server umgehen? Selber irgendwie fallback server bei join event bestimmen oder sowas

    @Override
    public void onPluginEnable() {
        networkManager = new NetworkManager<>(ServerType.PROXY,this);
        new PlayerProxyListener(networkManager);
        networkManager.getServerPingManager().sendOnlinePing();

        getServices().eventBus.register(new ServerPingListener());
        this.vCorePlayerAPI = new VCorePlayerBungeeImpl(this);
    }

    @Override
    public void onPluginDisable() {
        networkManager.getServerPingManager().sendOfflinePing();
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
    public String getServerName() {
        return networkManager.getServerPingManager().getServerName();
    }

    @Override
    public NetworkManager<VCoreWaterfall> getNetworkManager() {
        return networkManager;
    }
}
