package de.verdox.vcore.plugin;

import com.google.common.eventbus.EventBus;
import de.verdox.vcore.performance.concurrent.CatchingRunnable;
import de.verdox.vcore.performance.concurrent.TaskBatch;
import de.verdox.vcore.performance.concurrent.VCoreScheduler;
import de.verdox.vcore.synchronization.messaging.MessagingService;
import de.verdox.vcore.synchronization.messaging.redis.RedisMessaging;
import de.verdox.vcore.synchronization.pipeline.PipelineManager;
import de.verdox.vcore.synchronization.pipeline.PlayerDataManager;
import de.verdox.vcore.synchronization.pipeline.VCorePipelineConfig;
import de.verdox.vcore.synchronization.pipeline.annotations.RequiredSubsystemInfo;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import de.verdox.vcore.plugin.bukkit.BukkitPlugin;
import de.verdox.vcore.plugin.bungeecord.BungeeCordPlugin;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
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

    VCoreSubsystemManager<? extends VCorePlugin<T,R>,?> getSubsystemManager();

    EventBus getEventBus();
    //PlayerSessionManager<?> getSessionManager();
    //ServerDataManager<?> getServerDataManager();
    Pipeline getDataPipeline();
    MessagingService<?> getMessagingService();
    VCorePipelineConfig getVCorePipelineConfig();

    @Override
    default boolean isLoaded(){
        //return getServerDataManager().isLoaded() && getSessionManager().isLoaded() && getSubsystemManager().isLoaded();
        return getSubsystemManager().isLoaded();
    }

    default void registerVCoreEventListener(Object o){
        getEventBus().register(o);
    }

    default void unregisterVCoreEventListener(Object o){
        getEventBus().unregister(o);
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
        private final VCoreSubsystemManager<Minecraft,VCoreSubsystem.Bukkit> subsystemManager = new VCoreSubsystemManager<>(this);
        private final VCorePipelineConfig vCorePipelineConfig = new VCorePipelineConfig(getPluginDataFolder(), new File("VCorePipelineSettings.json"));
        private final Pipeline pipeline = vCorePipelineConfig.constructPipeline(this);
        private final RedisMessaging redisMessaging = new RedisMessaging(this, false, new String[]{"redis://127.0.0.1:6379"}, "");
        private PlayerDataManager playerDataManager;

        @Override
        public final void onEnable() {
            consoleMessage("&ePlugin starting&7!",false);
            onPluginEnable();
            subsystemManager.enable();
            playerDataManager = new PlayerDataManager.Bukkit((PipelineManager) pipeline);
            consoleMessage("&aPlugin started&7!",false);
        }

        @Override
        public VCorePipelineConfig getVCorePipelineConfig() {
            return vCorePipelineConfig;
        }

        @Override
        public Pipeline getDataPipeline() {
            return pipeline;
        }

        @Override
        public final void onDisable() {
            consoleMessage("&ePlugin stopping&7!",false);
            getDataPipeline().saveAllData();
            onPluginDisable();
            Bukkit.getWorlds().forEach(World::save);
            vCoreScheduler.waitUntilShutdown();
            consoleMessage("&aPlugin stopped&7!",false);
        }

        @Override
        public MessagingService<?> getMessagingService() {
            return redisMessaging;
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
        private final VCoreSubsystemManager<BungeeCord,VCoreSubsystem.BungeeCord> subsystemManager = new VCoreSubsystemManager<>(this);
        private final VCorePipelineConfig vCorePipelineConfig = new VCorePipelineConfig(getPluginDataFolder(), new File("VCorePipelineSettings.json"));
        private final Pipeline pipeline = vCorePipelineConfig.constructPipeline(this);
        private final RedisMessaging redisMessaging = new RedisMessaging(this, false, new String[]{"redis://127.0.0.1:6379"}, "");
        private PlayerDataManager playerDataManager;

        @Override
        public final void onEnable() {
            consoleMessage("&aPlugin starting&7!",false);
            onPluginEnable();
            subsystemManager.enable();
            playerDataManager = new PlayerDataManager.BungeeCord((PipelineManager) pipeline);
            consoleMessage("&aPlugin started&7!",false);
        }

        @Override
        public MessagingService<?> getMessagingService() {
            return redisMessaging;
        }

        @Override
        public Pipeline getDataPipeline() {
            return pipeline;
        }

        @Override
        public VCorePipelineConfig getVCorePipelineConfig() {
            return vCorePipelineConfig;
        }

        @Override
        public final void onDisable() {
            consoleMessage("&ePlugin stopping&7!",false);
            getDataPipeline().saveAllData();
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
        public EventBus getEventBus() {
            return eventBus;
        }

        @Override
        public VCoreSubsystemManager<BungeeCord, VCoreSubsystem.BungeeCord> getSubsystemManager() {
            return subsystemManager;
        }
    }
}
