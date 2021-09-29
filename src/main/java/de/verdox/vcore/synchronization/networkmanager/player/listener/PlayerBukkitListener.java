/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.listener;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.listener.VCoreListener;
import de.verdox.vcore.synchronization.networkmanager.NetworkManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 01.08.2021 02:52
 */
public class PlayerBukkitListener extends VCoreListener.VCoreBukkitListener implements VCorePlayerCacheListener {
    private final NetworkManager<?> networkManager;

    public PlayerBukkitListener(@NotNull NetworkManager<?> networkManager) {
        super((VCorePlugin.Minecraft) networkManager.getPlugin());
        Objects.requireNonNull(networkManager, "networkManager can't be null!");
        this.networkManager = networkManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        plugin.async(() -> {
            sendPlayerPing(getPlugin(),
                    networkManager.getServerPingManager().getServerName(),
                    networkManager.getServerType(), PlayerPingType.JOIN,
                    e.getPlayer().getUniqueId(),
                    e.getPlayer().getName());
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        plugin.async(() -> {
            sendPlayerPing(getPlugin(),
                    networkManager.getServerPingManager().getServerName(),
                    networkManager.getServerType(), PlayerPingType.QUIT,
                    e.getPlayer().getUniqueId(),
                    e.getPlayer().getName());
        });
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        plugin.async(() -> {
            sendPlayerPing(getPlugin(),
                    networkManager.getServerPingManager().getServerName(),
                    networkManager.getServerType(), PlayerPingType.KICK,
                    e.getPlayer().getUniqueId(),
                    e.getPlayer().getName());
        });
    }

}
