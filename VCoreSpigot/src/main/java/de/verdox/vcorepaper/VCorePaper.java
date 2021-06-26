package de.verdox.vcorepaper;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import de.verdox.vcore.synchronization.pipeline.PipelineManager;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import de.verdox.vcore.synchronization.pipeline.parts.cache.GlobalCache;
import de.verdox.vcore.synchronization.pipeline.parts.cache.redis.RedisCache;
import de.verdox.vcore.synchronization.pipeline.parts.local.LocalCache;
import de.verdox.vcore.synchronization.pipeline.parts.local.LocalCacheImpl;
import de.verdox.vcore.synchronization.pipeline.parts.storage.GlobalStorage;
import de.verdox.vcore.synchronization.pipeline.parts.storage.mongodb.MongoDBStorage;
import de.verdox.vcore.plugin.player.VCorePlayerManager;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import de.verdox.vcorepaper.commands.AdminCommands;
import de.verdox.vcorepaper.commands.NMSCommand;
import de.verdox.vcorepaper.custom.blocks.CustomBlockManager;
import de.verdox.vcorepaper.custom.blocks.VBlockListener;
import de.verdox.vcorepaper.custom.entities.CustomEntityListener;
import de.verdox.vcorepaper.custom.entities.CustomEntityManager;
import de.verdox.vcorepaper.custom.CustomDataListener;
import de.verdox.vcorepaper.custom.events.AsyncEventWrapper;
import de.verdox.vcorepaper.custom.items.CustomItemManager;
import de.verdox.vcorepaper.files.VCorePaperSettings;
import de.verdox.vcorepaper.nms.NMSManager;
import de.verdox.vcorepaper.bukkitplayerhandler.BukkitPlayerHandler;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.stream.Collectors;

public class VCorePaper extends VCorePlugin.Minecraft {
    public static VCorePaper instance;

    private VCorePlayerManager vCorePlayerManager;

    private NMSManager nmsManager;

    private CustomEntityManager customEntityManager;
    private CustomItemManager customItemManager;
    private CustomBlockManager customBlockManager;
    private ProtocolManager protocolManager;

    private VCorePaperSettings vCorePaperSettings;

    public static VCorePaper getInstance() {
        return instance;
    }

    @Override
    public void onPluginEnable() {
        instance = this;
        this.nmsManager = new NMSManager(this);
        this.vCorePlayerManager = new VCorePlayerManager(this);

        this.vCorePaperSettings = new VCorePaperSettings(this,"settings.yml","");
        this.vCorePaperSettings.init();

        this.customEntityManager = new CustomEntityManager(this);
        this.customItemManager = new CustomItemManager(this);
        this.customBlockManager = new CustomBlockManager(this);

        new CustomDataListener(this);
        new CustomEntityListener(this);
        new VBlockListener(this,customBlockManager);
        new AsyncEventWrapper(this);

        new AdminCommands(this,"debug");
        new NMSCommand(this,"nms");

        if(Bukkit.getPluginManager().getPlugin("ProtocolLib") != null)
            protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public ProtocolManager getProtocolManager() {
        if(Bukkit.getPluginManager().getPlugin("ProtocolLib") == null)
            throw new IllegalStateException("ProtocolLib could not be found on this server.");
        return protocolManager;
    }

    @Override
    public void onPluginDisable() {

    }

    @Override
    public List<VCoreSubsystem.Bukkit> provideSubsystems() {
        return List.of(new BukkitPlayerHandler(this));
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

    public CustomEntityManager getCustomEntityManager() {
        return customEntityManager;
    }

    public CustomItemManager getCustomItemManager() {
        return customItemManager;
    }

    public CustomBlockManager getCustomBlockManager() {
        return customBlockManager;
    }

    public void asyncThenSync(Runnable asyncTask, Runnable syncTask){
        async(() -> {
            asyncTask.run();
            sync(syncTask);
        });
    }

    public void syncThenAsync(Runnable syncTask, Runnable asyncTask){
        sync(() -> {
            syncTask.run();
            async(asyncTask);
        });
    }

    public VCorePlayerManager getVCorePlayerManager() {
        return vCorePlayerManager;
    }

    public NMSManager getNmsManager() {
        return nmsManager;
    }
}