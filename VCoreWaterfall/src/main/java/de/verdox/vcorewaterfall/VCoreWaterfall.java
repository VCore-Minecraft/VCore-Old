package de.verdox.vcorewaterfall;

import de.verdox.vcore.plugin.player.VCorePlayerManager;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;

import java.util.List;

public class VCoreWaterfall extends VCorePlugin.BungeeCord {

    private VCorePlayerManager vCorePlayerManager;

    @Override
    public void onPluginEnable() {

    }

    @Override
    public void onPluginDisable() {
        this.vCorePlayerManager = new VCorePlayerManager(this);
    }

    @Override
    public List<VCoreSubsystem.BungeeCord> provideSubsystems() {
        return null;
    }

    @Override
    public boolean useRedisCluster() {
        return false;
    }

    @Override
    public String[] redisAddresses() {
        return new String[0];
    }

    @Override
    public String redisPassword() {
        return null;
    }

    @Override
    public boolean debug() {
        return false;
    }

    @Override
    public Pipeline getDataPipeline() {
        return null;
    }

    public VCorePlayerManager getVCorePlayerManager() {
        return vCorePlayerManager;
    }
}
