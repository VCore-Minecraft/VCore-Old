package de.verdox.vcorewaterfall;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import de.verdox.vcore.synchronization.pipeline.player.VCorePlayerCache;
import de.verdox.vcorewaterfall.playercache.BungeePlayerCacheListener;

import java.util.List;

public class VCoreWaterfall extends VCorePlugin.BungeeCord {

    private VCorePlayerCache vCorePlayerCache;

    @Override
    public void onPluginEnable() {
        vCorePlayerCache = new VCorePlayerCache(this);
        new BungeePlayerCacheListener(this);
    }

    @Override
    public void onPluginDisable() {

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
