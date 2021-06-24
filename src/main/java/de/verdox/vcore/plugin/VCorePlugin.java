package de.verdox.vcore.plugin;

import com.google.common.eventbus.EventBus;
import de.verdox.vcore.concurrent.CatchingRunnable;
import de.verdox.vcore.concurrent.TaskBatch;
import de.verdox.vcore.data.manager.ServerDataManager;
import de.verdox.vcore.pipeline.annotations.RequiredSubsystemInfo;
import de.verdox.vcore.dataconnection.DataConnection;
import de.verdox.vcore.dataconnection.mongodb.annotation.MongoDBIdentifier;
import de.verdox.vcore.data.manager.PlayerSessionManager;
import de.verdox.vcore.pipeline.parts.Pipeline;
import de.verdox.vcore.plugin.bukkit.BukkitPlugin;
import de.verdox.vcore.plugin.bungeecord.BungeeCordPlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import net.md_5.bungee.api.plugin.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.List;

public interface VCorePlugin <T, R extends VCoreSubsystem<?>> extends SystemLoadable {

    void onPluginEnable();
    void onPluginDisable();
    List<R> provideSubsystems();
    boolean useRedisCluster();
    String[] redisAddresses();
    String redisPassword();

    T getPlugin();
    File getPluginDataFolder();
    String getPluginName();
    void consoleMessage(String message, boolean debug);
    void consoleMessage(String message, int tabSize, boolean debug);
    VCoreScheduler getScheduler();
    boolean debug();
    TaskBatch<VCorePlugin<T,R>> createTaskBatch();

    void async(Runnable runnable);
    void sync(Runnable runnable);

    DataConnection.MongoDB mongoDB();

    VCoreSubsystemManager<? extends VCorePlugin<T,R>,?> getSubsystemManager();

    EventBus getEventBus();
    PlayerSessionManager<?> getSessionManager();
    ServerDataManager<?> getServerDataManager();
    Pipeline getDataPipeline();

    @Override
    default boolean isLoaded(){
        return getServerDataManager().isLoaded() && getSessionManager().isLoaded() && getSubsystemManager().isLoaded();
    }

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

        private final VCoreScheduler vCoreScheduler = new VCoreScheduler(this);
        private final EventBus eventBus = new EventBus();
        private PlayerSessionManager.BukkitPlayerSessionManager sessionManager;
        private ServerDataManager<BukkitPlugin> serverDataManager;
        private final VCoreSubsystemManager<Minecraft,VCoreSubsystem.Bukkit> subsystemManager = new VCoreSubsystemManager<>(this);

        @Override
        public final void onEnable() {
            consoleMessage("&ePlugin starting&7!",false);
            onPluginEnable();
            subsystemManager.enable();

            getSessionManager();
            getServerDataManager();

            consoleMessage("&aPlugin started&7!",false);
        }

        @Override
        public final void onDisable() {
            consoleMessage("&ePlugin stopping&7!",false);
            getSessionManager().shutDown();
            getServerDataManager().shutDown();
            onPluginDisable();
            Bukkit.getWorlds().forEach(World::save);
            vCoreScheduler.waitUntilShutdown();
            consoleMessage("&aPlugin stopped&7!",false);
        }

        @Override
        public TaskBatch<VCorePlugin<JavaPlugin, VCoreSubsystem.Bukkit>> createTaskBatch() {

            return new TaskBatch<>(this) {
                @Override
                public void runSync(@Nonnull Runnable runnable) {
                    Bukkit.getScheduler().runTask(getPlugin().getPlugin(), runnable);
                }

                @Override
                public void runAsync(@Nonnull Runnable runnable) {
                    getScheduler().async(new CatchingRunnable(runnable));
                }

                @Override
                public void onFinishBatch() {

                }
            };
        }

        @Override
        public VCoreScheduler getScheduler() {
            return vCoreScheduler;
        }

        @Override
        public void async(Runnable runnable) {
            getScheduler().async(new CatchingRunnable(runnable));
        }

        @Override
        public void sync(Runnable runnable) {
            org.bukkit.Bukkit.getScheduler().runTask(this,runnable);
        }

        @Override
        public PlayerSessionManager<Minecraft> getSessionManager() {
            if(sessionManager == null)
                sessionManager = new PlayerSessionManager.BukkitPlayerSessionManager(this,useRedisCluster(),redisAddresses(), redisPassword(), mongoDB());
            return sessionManager;
        }

        @Override
        public ServerDataManager<BukkitPlugin> getServerDataManager() {
            if(serverDataManager == null)
                serverDataManager = new ServerDataManager<>(this,useRedisCluster(),redisAddresses(), redisPassword(), mongoDB());
            return serverDataManager;
        }

        @Override
        public EventBus getEventBus() {
            return eventBus;
        }

        @Override
        public VCoreSubsystemManager<Minecraft, VCoreSubsystem.Bukkit> getSubsystemManager() {
            return subsystemManager;
        }
    }
    abstract class BungeeCord extends BungeeCordPlugin {

        private final VCoreScheduler vCoreScheduler = new VCoreScheduler(this);
        private final EventBus eventBus = new EventBus();
        private PlayerSessionManager.BungeePlayerSessionManager sessionManager;
        private ServerDataManager<BungeeCordPlugin> serverDataManager;
        private final VCoreSubsystemManager<BungeeCord,VCoreSubsystem.BungeeCord> subsystemManager = new VCoreSubsystemManager<>(this);

        @Override
        public final void onEnable() {
            onPluginEnable();
            subsystemManager.enable();

            getSessionManager();
            getServerDataManager();

            consoleMessage("&aPlugin started&7!",false);
        }

        @Override
        public final void onDisable() {
            getSessionManager().saveAllData();
            getServerDataManager().saveAllData();
            onPluginDisable();
            subsystemManager.disable();
            vCoreScheduler.waitUntilShutdown();
            consoleMessage("&aPlugin started&7!",false);
        }

        @Override
        public TaskBatch<VCorePlugin<Plugin, VCoreSubsystem.BungeeCord>> createTaskBatch() {
            return null;
        }

        @Override
        public VCoreScheduler getScheduler() {
            return vCoreScheduler;
        }

        @Override
        public void async(Runnable runnable) {
            getScheduler().async(new CatchingRunnable(runnable));
        }

        @Override
        public void sync(Runnable runnable) {
            runnable.run();
        }

        @Override
        public PlayerSessionManager<BungeeCord> getSessionManager() {
            if(sessionManager == null)
                sessionManager = new PlayerSessionManager.BungeePlayerSessionManager(this,useRedisCluster(),redisAddresses(), redisPassword(),mongoDB());
            return sessionManager;
        }

        @Override
        public ServerDataManager<BungeeCordPlugin> getServerDataManager() {
            if(serverDataManager == null)
                serverDataManager = new ServerDataManager<>(this,useRedisCluster(),redisAddresses(), redisPassword(), mongoDB());
            return serverDataManager;
        }

        @Override
        public EventBus getEventBus() {
            return eventBus;
        }

        @Override
        public VCoreSubsystemManager<BungeeCord, VCoreSubsystem.BungeeCord> getSubsystemManager() {
            return subsystemManager;
        }
    }
}
