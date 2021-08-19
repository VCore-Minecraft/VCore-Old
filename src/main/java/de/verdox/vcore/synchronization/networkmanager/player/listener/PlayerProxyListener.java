/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.listener;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.listener.VCoreListener;
import de.verdox.vcore.synchronization.networkmanager.NetworkManager;
import de.verdox.vcore.synchronization.networkmanager.player.VCorePlayer;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;

import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 01.08.2021 02:53
 */
public class PlayerProxyListener extends VCoreListener.VCoreBungeeListener implements VCorePlayerCacheListener {
    private final NetworkManager<?> networkManager;

    public PlayerProxyListener(NetworkManager<?> networkManager) {
        super((VCorePlugin.BungeeCord) networkManager.getPlugin());
        this.networkManager = networkManager;
    }

    @net.md_5.bungee.event.EventHandler
    public void preLoginEvent(PreLoginEvent e) {
        String name = e.getConnection().getName();
        if (ProxyServer.getInstance().getPlayer(name) == null)
            return;
        e.setCancelled(true);
        e.setCancelReason(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&cDu bist bereits auf dem Netzwerk")));
    }

    @net.md_5.bungee.event.EventHandler
    public void loginEvent(LoginEvent e) {
        UUID uuid = e.getConnection().getUniqueId();
        if (uuid == null)
            return;
        if (!plugin.getServices().getPipeline().exist(VCorePlayer.class, uuid, Pipeline.QueryStrategy.LOCAL, Pipeline.QueryStrategy.GLOBAL_CACHE))
            return;
        e.setCancelled(true);
        e.setCancelReason(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&cDu bist bereits auf dem Netzwerk")));
    }

    @net.md_5.bungee.event.EventHandler
    public void onJoin(PostLoginEvent e) {
        plugin.async(() -> {
            sendPlayerPing(getPlugin(),
                    networkManager.getServerPingManager().getServerName(),
                    networkManager.getServerType(), PlayerPingType.JOIN,
                    e.getPlayer().getUniqueId(),
                    e.getPlayer().getName());
        });
    }

    @net.md_5.bungee.event.EventHandler
    public void onQuit(PlayerDisconnectEvent e) {
        plugin.async(() -> {
            sendPlayerPing(getPlugin(),
                    networkManager.getServerPingManager().getServerName(),
                    networkManager.getServerType(), PlayerPingType.QUIT,
                    e.getPlayer().getUniqueId(),
                    e.getPlayer().getName());
        });
    }
}