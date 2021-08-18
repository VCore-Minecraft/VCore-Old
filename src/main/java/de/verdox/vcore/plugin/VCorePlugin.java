package de.verdox.vcore.plugin;

import de.verdox.vcore.performance.concurrent.CatchingRunnable;
import de.verdox.vcore.performance.concurrent.TaskBatch;
import de.verdox.vcore.plugin.wrapper.BukkitPlatformWrapperImpl;
import de.verdox.vcore.plugin.wrapper.BungeePlatformWrapperImpl;
import de.verdox.vcore.plugin.wrapper.PlatformWrapper;
import de.verdox.vcore.synchronization.pipeline.annotations.RequiredSubsystemInfo;
import de.verdox.vcore.plugin.bukkit.BukkitPlugin;
import de.verdox.vcore.plugin.bungeecord.BungeeCordPlugin;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.List;

public interface VCorePlugin <T, R extends VCoreSubsystem<?>> extends SystemLoadable {
    String vCorePaperName = "VCorePaper";
    String vCoreWaterfallName = "VCoreWaterfall";

    void onPluginEnable();
    void onPluginDisable();

    List<R> provideSubsystems();

    T getPlugin();
    File getPluginDataFolder();
    String getPluginName();

    void consoleMessage(String message, boolean debug);
    void consoleMessage(String message, int tabSize, boolean debug);
    PlatformWrapper getPlatformWrapper();

    boolean debug();
    void setDebugMode(boolean value);

    TaskBatch<VCorePlugin<T,R>> createTaskBatch();
    void async(Runnable runnable);
    void sync(Runnable runnable);

    <V extends VCoreCoreInstance<T,R>> V getCoreInstance();

    PluginServiceParts<?,R> getServices();

    default VCoreSubsystem<?> findDependSubsystem(Class<?> classType){
        RequiredSubsystemInfo requiredSubsystemInfo = classType.getAnnotation(RequiredSubsystemInfo.class);
        if(requiredSubsystemInfo == null)
            throw new RuntimeException(classType.getName()+" does not have RequiredSubsystemInfo Annotation set");
        return getServices().getSubsystemManager().findSubsystemByClass(requiredSubsystemInfo.parentSubSystem());
    }

    abstract class Minecraft extends BukkitPlugin {

        private PluginServiceParts<VCorePlugin.Minecraft,VCoreSubsystem.Bukkit> serviceParts;
        private boolean loaded;

        @Override
        public <V extends VCoreCoreInstance<JavaPlugin, VCoreSubsystem.Bukkit>> V getCoreInstance() {
            return (V) Bukkit.getPluginManager().getPlugin(vCorePaperName);
        }

        @Override
        public PlatformWrapper getPlatformWrapper() {
            return new BukkitPlatformWrapperImpl();
        }

        @Override
        public void setDebugMode(boolean value){
            getServices().getDebugConfig().setDebugMode(value);
        }

        @Override
        public boolean debug() {
            if(getServices() == null)
                return true;
            if(getServices().getDebugConfig() == null)
                return true;
            return getServices().getDebugConfig().debugMode();
        }

        @Override
        public final void onEnable() {
            consoleMessage("&ePlugin starting&7!",false);
            serviceParts = new PluginServiceParts.Bukkit(this);
            serviceParts.enableBefore();
            onPluginEnable();
            serviceParts.enableAfter();
            consoleMessage("&aPlugin started&7!",false);
            loaded = true;
        }

        @Override
        public final void onDisable() {
            consoleMessage("&ePlugin stopping&7!",false);
            onPluginDisable();
            Bukkit.getWorlds().forEach(World::save);
            serviceParts.shutdown();
            consoleMessage("&aPlugin stopped&7!",false);
        }

        @Override
        public PluginServiceParts<?, VCoreSubsystem.Bukkit> getServices() {
            return serviceParts;
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
                   serviceParts.vCoreScheduler.async(new CatchingRunnable(runnable));
                }

                @Override
                public void onFinishBatch() {

                }
            };
        }

        @Override
        public void async(Runnable runnable) {
            serviceParts.vCoreScheduler.async(new CatchingRunnable(runnable));
        }

        @Override
        public void sync(Runnable runnable) {
            org.bukkit.Bukkit.getScheduler().runTask(this,runnable);
        }

        @Override
        public boolean isLoaded() {
            return loaded;
        }

        @Override
        public void shutdown() {
            onDisable();
        }
    }
    abstract class BungeeCord extends BungeeCordPlugin {

        private PluginServiceParts<VCorePlugin.BungeeCord,VCoreSubsystem.BungeeCord> serviceParts;
        private boolean loaded;

        @Override
        public <V extends VCoreCoreInstance<Plugin, VCoreSubsystem.BungeeCord>> V getCoreInstance() {
            return (V) ProxyServer.getInstance().getPluginManager().getPlugin(vCoreWaterfallName);
        }

        @Override
        public PlatformWrapper getPlatformWrapper() {
            return new BungeePlatformWrapperImpl();
        }

        @Override
        public void setDebugMode(boolean value){
            getServices().getDebugConfig().setDebugMode(value);
        }

        @Override
        public boolean debug() {
            if(getServices() == null)
                return true;
            if(getServices().getDebugConfig() == null)
                return true;
            return getServices().getDebugConfig().debugMode();
        }

        @Override
        public final void onEnable() {
            consoleMessage("&aPlugin starting&7!",false);
            serviceParts = new PluginServiceParts.BungeeCord(this);
            serviceParts.enableBefore();
            onPluginEnable();
            serviceParts.enableAfter();
            consoleMessage("&aPlugin started&7!",false);
            loaded = true;
        }

        @Override
        public final void onDisable() {
            consoleMessage("&ePlugin stopping&7!",false);
            onPluginDisable();
            serviceParts.shutdown();
            consoleMessage("&aPlugin started&7!",false);
        }

        @Override
        public boolean isLoaded() {
            return loaded;
        }

        @Override
        public void shutdown() {
            onDisable();
        }

        @Override
        public TaskBatch<VCorePlugin<Plugin, VCoreSubsystem.BungeeCord>> createTaskBatch() {
            return new TaskBatch<>(this) {
                @Override
                public void runSync(@Nonnull Runnable runnable) {
                    runnable.run();
                }

                @Override
                public void runAsync(@Nonnull Runnable runnable) {
                    serviceParts.vCoreScheduler.async(new CatchingRunnable(runnable));
                }

                @Override
                public void onFinishBatch() {

                }
            };
        }

        @Override
        public void async(Runnable runnable) {
            serviceParts.vCoreScheduler.async(new CatchingRunnable(runnable));
        }

        @Override
        public PluginServiceParts<?, VCoreSubsystem.BungeeCord> getServices() {
            return serviceParts;
        }

        @Override
        public void sync(Runnable runnable) {
            runnable.run();
        }
    }
}
