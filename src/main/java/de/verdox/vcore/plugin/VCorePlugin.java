package de.verdox.vcore.plugin;

import com.google.common.eventbus.EventBus;
import de.verdox.vcore.data.manager.ServerDataManager;
import de.verdox.vcore.data.annotations.RequiredSubsystemInfo;
import de.verdox.vcore.dataconnection.DataConnection;
import de.verdox.vcore.dataconnection.mongodb.annotation.MongoDBIdentifier;
import de.verdox.vcore.data.manager.PlayerSessionManager;
import de.verdox.vcore.plugin.bukkit.BukkitPlugin;
import de.verdox.vcore.plugin.bungeecord.BungeeCordPlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import net.md_5.bungee.api.ProxyServer;

import java.io.File;
import java.util.List;

public interface VCorePlugin <T, R extends VCoreSubsystem<?>> {

    void onPluginEnable();
    void onPluginDisable();
    List<R> provideSubsystems();
    boolean useRedisCluster();
    String[] redisAddresses();

    T getPlugin();
    File getPluginDataFolder();
    String getPluginName();
    void consoleMessage(String message);

    void async(Runnable runnable);
    void sync(Runnable runnable);

    DataConnection.MongoDB mongoDB();

    VCoreSubsystemManager<? extends VCorePlugin<T,R>,?> getSubsystemManager();

    EventBus getEventBus();
    PlayerSessionManager<?> getSessionManager();
    ServerDataManager<?> getServerDataManager();

    default void registerVCoreEventListener(Object o){
        getEventBus().register(o);
    }

    default void unregisterVCoreEventListener(Object o){
        getEventBus().unregister(o);
    }

    static String getMongoDBIdentifier(Class<?> customClass){
        MongoDBIdentifier mongoDBIdentifier = customClass.getAnnotation(MongoDBIdentifier.class);
        if(mongoDBIdentifier == null)
            throw new NullPointerException("MongoDBIdentifier not set for class: "+customClass);
        return mongoDBIdentifier.identifier();
    }

    static Class<? extends VCoreSubsystem<?>> findDependSubsystemClass(Class<?> classType){
        RequiredSubsystemInfo requiredSubsystemInfo = classType.getAnnotation(RequiredSubsystemInfo.class);
        if(requiredSubsystemInfo == null)
            throw new RuntimeException(classType.getName()+" does not have RequiredSubsystemInfo Annotation set");
        return requiredSubsystemInfo.parentSubSystem();
    }

    default VCoreSubsystem<?> findDependSubsystem(Class<?> classType){
        RequiredSubsystemInfo requiredSubsystemInfo = classType.getAnnotation(RequiredSubsystemInfo.class);
        if(requiredSubsystemInfo == null)
            throw new RuntimeException(classType.getName()+" does not have RequiredSubsystemInfo Annotation set");
        return getSubsystemManager().findSubsystemByClass(requiredSubsystemInfo.parentSubSystem());
    }

    abstract class Minecraft extends BukkitPlugin{

        private EventBus eventBus;
        private PlayerSessionManager.BukkitPlayerSessionManager sessionManager;
        private ServerDataManager<BukkitPlugin> serverDataManager;
        private final VCoreSubsystemManager<Minecraft,VCoreSubsystem.Bukkit> subsystemManager = new VCoreSubsystemManager<>(this);

        @Override
        public final void onEnable() {
            consoleMessage("&eStarting VCorePlugin&7: &a"+getPluginName());
            onPluginEnable();
            subsystemManager.enable();
        }

        @Override
        public final void onDisable() {
            consoleMessage("&eStopping VCorePlugin&7: &a"+getPluginName());
            onPluginDisable();
            subsystemManager.disable();
        }

        @Override
        public void async(Runnable runnable) {
            org.bukkit.Bukkit.getScheduler().runTaskAsynchronously(this,runnable);
        }

        @Override
        public void sync(Runnable runnable) {
            org.bukkit.Bukkit.getScheduler().runTask(this,runnable);
        }

        @Override
        public PlayerSessionManager<Minecraft> getSessionManager() {
            if(sessionManager == null)
                sessionManager = new PlayerSessionManager.BukkitPlayerSessionManager(this,useRedisCluster(),redisAddresses(),mongoDB());
            return sessionManager;
        }

        @Override
        public ServerDataManager<BukkitPlugin> getServerDataManager() {
            if(serverDataManager == null)
                serverDataManager = new ServerDataManager<>(this,useRedisCluster(),redisAddresses(),mongoDB());
            return serverDataManager;
        }

        @Override
        public EventBus getEventBus() {
            if(eventBus == null)
                eventBus = new EventBus();
            return eventBus;
        }

        @Override
        public VCoreSubsystemManager<Minecraft, VCoreSubsystem.Bukkit> getSubsystemManager() {
            return subsystemManager;
        }
    }
    abstract class BungeeCord extends BungeeCordPlugin {

        private EventBus eventBus;
        private PlayerSessionManager.BungeePlayerSessionManager sessionManager;
        private ServerDataManager<BungeeCordPlugin> serverDataManager;
        private final VCoreSubsystemManager<BungeeCord,VCoreSubsystem.BungeeCord> subsystemManager = new VCoreSubsystemManager<>(this);

        @Override
        public final void onEnable() {
            onPluginEnable();
            subsystemManager.enable();
        }

        @Override
        public final void onDisable() {
            onPluginDisable();
            subsystemManager.disable();
        }

        @Override
        public void async(Runnable runnable) {
            ProxyServer.getInstance().getScheduler().runAsync(this,runnable);
        }

        @Override
        public void sync(Runnable runnable) {
            runnable.run();
        }

        @Override
        public PlayerSessionManager<BungeeCord> getSessionManager() {
            if(sessionManager == null)
                sessionManager = new PlayerSessionManager.BungeePlayerSessionManager(this,useRedisCluster(),redisAddresses(),mongoDB());
            return sessionManager;
        }

        @Override
        public ServerDataManager<BungeeCordPlugin> getServerDataManager() {
            if(serverDataManager == null)
                serverDataManager = new ServerDataManager<>(this,useRedisCluster(),redisAddresses(),mongoDB());
            return serverDataManager;
        }

        @Override
        public EventBus getEventBus() {
            if(eventBus == null)
                eventBus = new EventBus();
            return eventBus;
        }

        @Override
        public VCoreSubsystemManager<BungeeCord, VCoreSubsystem.BungeeCord> getSubsystemManager() {
            return subsystemManager;
        }
    }
}
