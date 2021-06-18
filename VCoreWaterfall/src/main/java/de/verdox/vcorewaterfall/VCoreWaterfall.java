package de.verdox.vcorewaterfall;

import de.verdox.vcore.dataconnection.DataConnection;
import de.verdox.vcore.player.VCorePlayerManager;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;

import java.util.List;

public class VCoreWaterfall extends VCorePlugin.BungeeCord {

    private VCorePlayerManager vCorePlayerManager;

    @Override
    public void onPluginEnable() {
        getSessionManager();
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
    public DataConnection.MongoDB mongoDB() {
        return null;
    }

    public VCorePlayerManager getvCorePlayerManager() {
        return vCorePlayerManager;
    }
}
