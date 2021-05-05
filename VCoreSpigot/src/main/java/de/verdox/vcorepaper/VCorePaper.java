package de.verdox.vcorepaper;

import de.verdox.vcore.dataconnection.DataConnection;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import de.verdox.vcore.subsystem.exceptions.SubsystemDeactivatedException;
import de.verdox.vcorepaper.commands.ConsoleCommands;
import de.verdox.vcorepaper.custom.entities.CustomEntityManager;
import de.verdox.vcorepaper.custom.items.CustomItemManager;
import de.verdox.vcorepaper.files.VCorePaperSettings;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.arguments.*;

import java.util.List;
import java.util.stream.Collectors;

public class VCorePaper extends VCorePlugin.Minecraft {
    public static VCorePaper instance;

    private DataConnection.MongoDB mongoDB;

    private CustomEntityManager customEntityManager;
    private CustomItemManager customItemManager;

    private VCorePaperSettings vCorePaperSettings;

    public static VCorePaper getInstance() {
        return instance;
    }

    @Override
    public void onPluginEnable() {
        instance = this;

        this.vCorePaperSettings = new VCorePaperSettings(this,"settings.yml","");
        this.vCorePaperSettings.init();

        getSessionManager();
        getServerDataManager();

        this.customEntityManager = new CustomEntityManager(this);
        this.customItemManager = new CustomItemManager(this);

        getCommand("debug").setExecutor(new ConsoleCommands());
    }

    @Override
    public void onPluginDisable() {

    }

    @Override
    public List<VCoreSubsystem.Bukkit> provideSubsystems() {
        return null;
    }

    @Override
    public boolean useRedisCluster() {
        return vCorePaperSettings.useRedisCluster();
    }

    @Override
    public String[] redisAddresses() { return vCorePaperSettings.getRedisAddresses().stream().map(address -> "redis://"+address).collect(Collectors.toList()).toArray(new String[0]); }

    @Override
    public String redisPassword() {
        return vCorePaperSettings.getRedisPassword();
    }

    @Override
    public boolean debug() {
        return false;
    }

    @Override
    public DataConnection.MongoDB mongoDB() {
        if(mongoDB == null)
            return new DataConnection.MongoDB(this
                    ,vCorePaperSettings.getMongoDBHost()
                    , vCorePaperSettings.getMongoDBDatabase()
                    , vCorePaperSettings.getMongoDBPort()
                    ,vCorePaperSettings.getMongoDBUsername()
                    ,vCorePaperSettings.getMongoDBPassword()) {
                @Override
                public void onConnect() {
                    consoleMessage("&eMongoDB successfully connected!",false);
                }

                @Override
                public void onDisconnect() { }
            };
        return mongoDB;
    }

    public CustomEntityManager getCustomEntityManager() {
        return customEntityManager;
    }

    public CustomItemManager getCustomItemManager() {
        return customItemManager;
    }
}