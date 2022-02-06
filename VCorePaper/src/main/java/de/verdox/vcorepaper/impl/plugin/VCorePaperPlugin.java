/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.impl.plugin;

import de.verdox.vcore.performance.concurrent.CatchingRunnable;
import de.verdox.vcore.performance.concurrent.TaskBatch;
import de.verdox.vcore.plugin.PluginServiceParts;
import de.verdox.vcore.plugin.VCoreCoreInstance;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.wrapper.PlatformWrapper;
import de.verdox.vcorepaper.impl.platform.PaperPlatformWrapperImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

public abstract class VCorePaperPlugin extends JavaPlugin implements VCorePlugin<JavaPlugin, VCorePaperSubsystem> {

    private PluginServiceParts<VCorePaperPlugin, VCorePaperSubsystem> serviceParts;
    private boolean loaded;

    @Override
    public final PlatformWrapper getPlatformWrapper() {
        return new PaperPlatformWrapperImpl();
    }

    @Override
    public final void setDebugMode(boolean value) {
        getServices().getDebugConfig().setDebugMode(value);
    }

    @Override
    public final boolean debug() {
        if (getServices() == null)
            return true;
        if (getServices().getDebugConfig() == null)
            return true;
        return getServices().getDebugConfig().debugMode();
    }

    @Override
    public final  <V extends VCoreCoreInstance<JavaPlugin, VCorePaperSubsystem>> V getCoreInstance() {
        return (V) Bukkit.getPluginManager().getPlugin(vCorePaperName);
    }

    @Override
    public final void onEnable() {
        consoleMessage("&ePlugin starting&7!", false);
        serviceParts = new VCorePaperServiceParts(this);
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
        Bukkit.getWorlds().forEach(World::save);
        serviceParts.shutdown();
        consoleMessage("&aPlugin stopped&7!", false);
    }

    @Override
    public PluginServiceParts<?, VCorePaperSubsystem> getServices() {
        return serviceParts;
    }

    @Override
    public TaskBatch<VCorePlugin<JavaPlugin, VCorePaperSubsystem>> createTaskBatch() {
        return new TaskBatch<>(this) {
            @Override
            public void runSync(@NotNull Runnable runnable) {
                Bukkit.getScheduler().runTask(getPlugin().getPlugin(), runnable);
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
    public void sync(Runnable runnable) {
        org.bukkit.Bukkit.getScheduler().runTask(this, runnable);
    }

    @Override
    public final boolean isLoaded() {
        return loaded;
    }

    @Override
    public final void shutdown() {
        onDisable();
    }

    @Override
    public File getPluginDataFolder() {
        return getPlugin().getDataFolder();
    }

    @Override
    public JavaPlugin getPlugin() {
        return this;
    }

    @Override
    public String getPluginName() {
        return getPlugin().getName();
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
        org.bukkit.Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&c" + getPluginName() + "&8] " + newMessageBuilder));
    }
}
