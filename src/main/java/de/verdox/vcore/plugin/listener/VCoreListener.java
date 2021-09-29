/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.listener;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import net.md_5.bungee.api.ProxyServer;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class VCoreListener<T extends VCorePlugin<?, ?>> {

    protected final T plugin;

    public VCoreListener(@NotNull VCoreSubsystem<T> subsystem) {
        Objects.requireNonNull(subsystem, "subsystem can't be null!");
        plugin = subsystem.getVCorePlugin();

        registerListener();
    }

    public VCoreListener(@NotNull T plugin) {
        Objects.requireNonNull(plugin, "plugin can't be null!");
        this.plugin = plugin;
        registerListener();
    }

    protected abstract void registerListener();

    public T getPlugin() {
        return plugin;
    }

    public static class VCoreBukkitListener extends VCoreListener<VCorePlugin.Minecraft> implements Listener {

        public VCoreBukkitListener(@NotNull VCoreSubsystem<VCorePlugin.Minecraft> subsystem) {
            super(subsystem);
        }

        public VCoreBukkitListener(@NotNull VCorePlugin.Minecraft plugin) {
            super(plugin);
        }

        @Override
        protected void registerListener() {
            getPlugin().consoleMessage("&eRegistering Listener&7: &b" + getClass().getSimpleName(), false);
            Bukkit.getPluginManager().registerEvents(this, getPlugin().getPlugin());
        }
    }

    public static class VCoreBungeeListener extends VCoreListener<VCorePlugin.BungeeCord> implements net.md_5.bungee.api.plugin.Listener {

        public VCoreBungeeListener(VCoreSubsystem<VCorePlugin.BungeeCord> subsystem) {
            super(subsystem);
        }

        public VCoreBungeeListener(VCorePlugin.BungeeCord plugin) {
            super(plugin);
        }

        @Override
        protected void registerListener() {
            getPlugin().consoleMessage("&eRegistering Listener&7: &b" + getClass().getSimpleName(), false);
            ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
        }
    }
}
