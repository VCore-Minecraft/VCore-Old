package de.verdox.vcorewaterfall;

import de.verdox.vcore.dataconnection.DataConnection;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;

import java.util.List;

public class VCoreWaterfall extends VCorePlugin.BungeeCord {
    @Override
    public void onPluginEnable() {
        getSessionManager();
    }

    @Override
    public void onPluginDisable() {

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
    public DataConnection.MongoDB mongoDB() {
        return null;
    }
}
