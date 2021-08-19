/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin;

import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import de.verdox.vcore.synchronization.networkmanager.NetworkManager;
import de.verdox.vcore.synchronization.networkmanager.player.api.VCorePlayerAPI;
import net.md_5.bungee.api.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 03.08.2021 20:28
 */
public interface VCoreCoreInstance<T, R extends VCoreSubsystem<?>> extends VCorePlugin<T, R> {
    VCorePlayerAPI getPlayerAPI();

    <X extends VCorePlugin<T, R>> NetworkManager<X> getNetworkManager();

    String getServerName();

    abstract class Minecraft extends VCorePlugin.Minecraft implements VCoreCoreInstance<JavaPlugin, VCoreSubsystem.Bukkit> {

    }

    abstract class BungeeCord extends VCorePlugin.BungeeCord implements VCoreCoreInstance<Plugin, VCoreSubsystem.BungeeCord> {

    }
}
