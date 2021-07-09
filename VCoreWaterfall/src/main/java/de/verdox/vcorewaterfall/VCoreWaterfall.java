package de.verdox.vcorewaterfall;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.pingservice.ServerPingManager;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import de.verdox.vcore.synchronization.pipeline.player.VCorePlayerCache;
import de.verdox.vcorewaterfall.pings.ServerPingListener;
import de.verdox.vcorewaterfall.playercache.BungeePlayerCacheListener;

import java.util.List;

public class VCoreWaterfall extends VCorePlugin.BungeeCord {

    private VCorePlayerCache vCorePlayerCache;
    private ServerPingManager.BungeeCord serverPingManager;

    @Override
    public void onPluginEnable() {
        vCorePlayerCache = new VCorePlayerCache(this);
        new BungeePlayerCacheListener(this);
        getServices().eventBus.register(new ServerPingListener());
        serverPingManager = new ServerPingManager.BungeeCord(this);
        serverPingManager.sendOnlinePing();
    }

    public ServerPingManager.BungeeCord getServerPingManager() {
        return serverPingManager;
    }

    @Override
    public void onPluginDisable() {
        serverPingManager.sendOfflinePing();
    }

    @Override
    public List<VCoreSubsystem.BungeeCord> provideSubsystems() {
        return null;
    }

    @Override
    public boolean debug() {
        return false;
    }

    public VCorePlayerCache getVCorePlayerCache() {
        return vCorePlayerCache;
    }
}
