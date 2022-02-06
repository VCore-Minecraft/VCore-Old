package de.verdox.vcorewaterfall.impl.plugin;

import de.verdox.vcore.performance.concurrent.CatchingRunnable;
import de.verdox.vcore.performance.concurrent.TaskBatch;
import de.verdox.vcore.plugin.PluginServiceParts;
import de.verdox.vcore.plugin.VCoreCoreInstance;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.wrapper.PlatformWrapper;
import de.verdox.vcorewaterfall.impl.platform.WaterfallPlatformWrapperImpl;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.02.2022 22:34
 */
public abstract class VCoreWaterfallPlugin extends Plugin implements VCorePlugin<Plugin, VCoreWaterfallSubsystem> {


    private PluginServiceParts<VCoreWaterfallPlugin, VCoreWaterfallSubsystem> serviceParts;
    private boolean loaded;

    @Override
    public <V extends VCoreCoreInstance<Plugin, VCoreWaterfallSubsystem>> V getCoreInstance() {
        return (V) ProxyServer.getInstance().getPluginManager().getPlugin(vCoreWaterfallName);
    }

    @Override
    public PlatformWrapper getPlatformWrapper() {
        return new WaterfallPlatformWrapperImpl();
    }

    @Override
    public void setDebugMode(boolean value) {
        getServices().getDebugConfig().setDebugMode(value);
    }

    @Override
    public boolean debug() {
        if (getServices() == null)
            return true;
        if (getServices().getDebugConfig() == null)
            return true;
        return getServices().getDebugConfig().debugMode();
    }

    @Override
    public final void onEnable() {
        consoleMessage("&aPlugin starting&7!", false);
        serviceParts = new VCoreWaterfallServiceParts(this);
        serviceParts.enableBefore();
        onPluginEnable();
        serviceParts.enableAfter();
        consoleMessage("&aPlugin started&7!", false);
        loaded = true;
    }

    @Override
    public final void onDisable() {
        consoleMessage("&ePlugin stopping&7!", false);
        onPluginDisable();
        serviceParts.shutdown();
        consoleMessage("&aPlugin started&7!", false);
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
    public TaskBatch<VCorePlugin<Plugin, VCoreWaterfallSubsystem>> createTaskBatch() {
        return new TaskBatch<>(this) {
            @Override
            public void runSync(@NotNull Runnable runnable) {
                runnable.run();
            }

            @Override
            public void runAsync(@NotNull Runnable runnable) {
                serviceParts.getVCoreScheduler().async(new CatchingRunnable(runnable));
            }

            @Override
            public void onFinishBatch() {

            }
        };
    }

    @Override
    public void async(Runnable runnable) {
        serviceParts.getVCoreScheduler().async(new CatchingRunnable(runnable));
    }

    @Override
    public PluginServiceParts<?, VCoreWaterfallSubsystem> getServices() {
        return serviceParts;
    }

    @Override
    public void sync(Runnable runnable) {
        runnable.run();
    }

    @Override
    public File getPluginDataFolder() {
        return getPlugin().getDataFolder();
    }

    @Override
    public String getPluginName() {
        return getPlugin().getDescription().getName();
    }

    @Override
    public Plugin getPlugin() {
        return this;
    }

    @Override
    public void consoleMessage(String message, boolean debug) {
        Objects.requireNonNull(message, "message can't be null!");
        if (debug && !debug())
            return;
        consoleMessage(message, 0, debug);
    }

    @Override
    public void consoleMessage(@NotNull String message, int tabSize, boolean debug) {
        Objects.requireNonNull(message, "message can't be null!");
        if (debug && !debug())
            return;
        StringBuilder newMessageBuilder = new StringBuilder();
        for (int i = 0; i < tabSize; i++)
            newMessageBuilder.append("\t");
        if (tabSize >= 1)
            newMessageBuilder.append("&7>> &f");
        newMessageBuilder.append(message);
        getPlugin().getProxy().getConsole().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&c" + getPluginName() + "&8] " + newMessageBuilder));

    }
}
