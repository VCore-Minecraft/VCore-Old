/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorewaterfall.playercache;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.listener.VCoreListener;
import de.verdox.vcore.synchronization.messaging.messages.Message;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.event.EventHandler;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 02.07.2021 17:26
 */
public class BungeePlayerCacheListener extends VCoreListener.VCoreBungeeListener {
    public BungeePlayerCacheListener(VCorePlugin.BungeeCord plugin) {
        super(plugin);
    }

    @EventHandler
    public void onJoin(PostLoginEvent e){
        plugin.async(() -> {
            Message message = plugin.getServices().getMessagingService().constructMessage()
                    .withParameters("connection", "bungee", "join")
                    .withData(e.getPlayer().getUniqueId(), e.getPlayer().getName())
                    .constructMessage();
            plugin.getServices().getMessagingService().publishMessage(message);
        });
    }

    @EventHandler
    public void onPreJoin(PostLoginEvent e){
        int maxPlayers = 50;
        if(ProxyServer.getInstance().getOnlineCount() > maxPlayers){
            if(!e.getPlayer().hasPermission("vcore.fullJoin"))
                e.getPlayer().disconnect(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&b&lBlock95 &6Der Server ist aktuell voll. \n&6Bitte versuche es später erneut.")));
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerDisconnectEvent e){
        plugin.async(() -> {
            Message message = plugin.getServices().getMessagingService().constructMessage()
                    .withParameters("connection", "bungee", "leave")
                    .withData(e.getPlayer().getUniqueId(), e.getPlayer().getName())
                    .constructMessage();
            plugin.getServices().getMessagingService().publishMessage(message);
        });
    }
}
