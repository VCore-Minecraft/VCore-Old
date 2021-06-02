package de.verdox.vcorepaper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import de.verdox.vcore.dataconnection.DataConnection;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import de.verdox.vcorepaper.commands.AdminCommands;
import de.verdox.vcorepaper.commands.ConsoleCommands;
import de.verdox.vcorepaper.custom.blocks.VBlockListener;
import de.verdox.vcorepaper.custom.blocks.VBlockManager;
import de.verdox.vcorepaper.custom.entities.CustomEntityListener;
import de.verdox.vcorepaper.custom.entities.CustomEntityManager;
import de.verdox.vcorepaper.custom.CustomDataListener;
import de.verdox.vcorepaper.custom.events.AsyncEventWrapper;
import de.verdox.vcorepaper.custom.items.CustomItemManager;
import de.verdox.vcorepaper.files.VCorePaperSettings;

import java.util.List;
import java.util.stream.Collectors;

public class VCorePaper extends VCorePlugin.Minecraft {
    public static VCorePaper instance;

    private DataConnection.MongoDB mongoDB;

    private CustomEntityManager customEntityManager;
    private CustomItemManager customItemManager;
    private VBlockManager vBlockManager;
    private ProtocolManager protocolManager;

    private VCorePaperSettings vCorePaperSettings;

    public static VCorePaper getInstance() {
        return instance;
    }

    @Override
    public void onPluginEnable() {
        instance = this;


        this.vCorePaperSettings = new VCorePaperSettings(this,"settings.yml","");
        this.vCorePaperSettings.init();

        this.customEntityManager = new CustomEntityManager(this);
        this.customItemManager = new CustomItemManager(this);
        this.vBlockManager = new VBlockManager(this);

        new CustomDataListener(this);
        new CustomEntityListener(this);
        new VBlockListener(this,vBlockManager);
        new AsyncEventWrapper(this);

        getSessionManager();
        getServerDataManager();


        getCommand("debug").setExecutor(new ConsoleCommands());
        new AdminCommands(this,"debug");
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
        return true;
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

    public VBlockManager getVBlockManager() {
        return vBlockManager;
    }
}