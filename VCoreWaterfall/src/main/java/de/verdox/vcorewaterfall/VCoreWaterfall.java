/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcorewaterfall;

import de.verdox.vcore.modules.VCoreModuleLoader;
import de.verdox.vcore.modules.VCoreModuleLoaderImpl;
import de.verdox.vcore.plugin.VCoreCoreInstance;
import de.verdox.vcore.synchronization.networkmanager.NetworkManager;
import de.verdox.vcore.synchronization.networkmanager.player.VCorePlayer;
import de.verdox.vcore.synchronization.networkmanager.player.api.VCorePlayerAPI;
import de.verdox.vcore.synchronization.networkmanager.server.ServerType;
import de.verdox.vcore.synchronization.networkmanager.server.api.VCoreServerAPI;
import de.verdox.vcore.synchronization.networkmanager.server.api.VCoreServerAPIImpl;
import de.verdox.vcorewaterfall.impl.module.VCoreWaterfallModule;
import de.verdox.vcorewaterfall.impl.listener.VCoreWaterfallPlayerPingListener;
import de.verdox.vcorewaterfall.impl.network.VCoreWaterfallPlayerAPIImpl;
import de.verdox.vcorewaterfall.impl.plugin.VCoreWaterfallPlugin;
import de.verdox.vcorewaterfall.impl.plugin.VCoreWaterfallSubsystem;
import de.verdox.vcorewaterfall.pings.ServerPingListener;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.List;

public class VCoreWaterfall extends VCoreWaterfallPlugin implements VCoreCoreInstance<Plugin, VCoreWaterfallSubsystem> {
    public static VCoreWaterfall instance;

    private NetworkManager<VCoreWaterfall> networkManager;
    private VCorePlayerAPI vCorePlayerAPI;
    private VCoreServerAPI vCoreServerAPI;

    private VCoreModuleLoader<Plugin, VCoreWaterfallSubsystem, VCoreWaterfall, VCoreWaterfallModule> moduleLoader;

    //TODO: BungeeCord Fallback Server umgehen? Selber irgendwie fallback server bei join event bestimmen oder sowas

    @Override
    public void onPluginEnable() {
        instance = this;
        //metrics = new Metrics(this, 12913);
        networkManager = new NetworkManager<>(this, ServerType.PROXY);
        new VCoreWaterfallPlayerPingListener(networkManager);
        moduleLoader = new VCoreModuleLoaderImpl<>(this);

        getServices().eventBus.register(new ServerPingListener(this));
        this.vCorePlayerAPI = new VCoreWaterfallPlayerAPIImpl(this);
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
    public List<VCoreWaterfallSubsystem> provideSubsystems() {
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
    public VCoreModuleLoader<Plugin, VCoreWaterfallSubsystem, VCoreWaterfall, VCoreWaterfallModule> getModuleLoader() {
        return moduleLoader;
    }

    @Override
    public NetworkManager<VCoreWaterfall> getNetworkManager() {
        return networkManager;
    }

    public static VCoreWaterfall getInstance() {
        return instance;
    }
}
