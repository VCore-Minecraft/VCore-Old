/*
 * Copyright (c) 2022. Lukas Jonsson
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
}
