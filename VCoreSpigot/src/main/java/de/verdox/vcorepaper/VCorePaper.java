package de.verdox.vcorepaper;

import de.verdox.vcore.dataconnection.DataConnection;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import de.verdox.vcorepaper.custom.entities.CustomEntityManager;
import de.verdox.vcorepaper.custom.items.CustomItemManager;
import de.verdox.vcorepaper.subsystems.VCoreTestSubsystem;

import java.util.Collections;
import java.util.List;

public class VCorePaper extends VCorePlugin.Minecraft {
    public static VCorePaper instance;

    private final CustomEntityManager customEntityManager;
    private final CustomItemManager customItemManager;

    public static VCorePaper getInstance() {
        return instance;
    }

    public VCorePaper() {
        this.customEntityManager = new CustomEntityManager(this);
        this.customItemManager = new CustomItemManager(this);
    }

    @Override
    public void onPluginEnable() {
        instance = this;
        getSessionManager();
        getServerDataManager();
    }

    @Override
    public void onPluginDisable() {

    }

    @Override
    public List<VCoreSubsystem.Bukkit> provideSubsystems() {
        return Collections.singletonList(new VCoreTestSubsystem(this));
    }

    @Override
    public boolean useRedisCluster() {
        return false;
    }

    @Override
    public String[] redisAddresses() { return new String[]{"redis://localhost:6379"}; }

    @Override
    public DataConnection.MongoDB mongoDB() {
        return new DataConnection.MongoDB(this,"localhost","vcore",27017,"","") {
            @Override
            public void onConnect() {
                consoleMessage("&eMongoDB successfully connected!");
            }

            @Override
            public void onDisconnect() { }
        };
    }

    public CustomEntityManager getCustomEntityManager() {
        return customEntityManager;
    }
}