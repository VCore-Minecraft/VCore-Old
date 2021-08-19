/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorewaterfall.pings;

import com.google.common.eventbus.Subscribe;
import de.verdox.vcore.synchronization.networkmanager.server.ServerType;
import de.verdox.vcore.synchronization.networkmanager.serverping.events.ServerPingOfflineEvent;
import de.verdox.vcore.synchronization.networkmanager.serverping.events.ServerPingOnlineEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 09.07.2021 00:43
 */
public class ServerPingListener {

    @Subscribe
    public void pingOnlineEvent(ServerPingOnlineEvent e){
        if(!e.isFirstReceivedPing())
            return;
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if(player.hasPermission("vCore.notifyServers"))
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&8[&6VCore&8] &e"+e.getServerName()+" &aONLINE"));
        }
        if(e.getServerType().equals(ServerType.GAME_SERVER))
            ProxyServer.getInstance().getConsole().sendMessage(ChatColor.translateAlternateColorCodes('&',"&8[&6VCore&8] &e"+e.getServerName()+" &aONLINE"));
        if(!ProxyServer.getInstance().getServersCopy().containsKey(e.getServerName()) && e.getServerType().equals(ServerType.GAME_SERVER)){
            ServerInfo serverInfo = ProxyServer.getInstance().constructServerInfo(e.getServerName(), new InetSocketAddress(e.getServerAddress(),e.getServerPort()),e.getServerName(),false);
            ProxyServer.getInstance().getServers().put(serverInfo.getName(),serverInfo);
        }
    }

    @Subscribe
    public void pingOfflineEvent(ServerPingOfflineEvent e){
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if(player.hasPermission("vCore.notifyServers"))
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&8[&6VCore&8] &e"+e.getServerName()+" &cOFFLINE"));
        }
        ProxyServer.getInstance().getConsole().sendMessage(ChatColor.translateAlternateColorCodes('&',"&8[&6VCore&8] &e"+e.getServerName()+" &cOFFLINE"));

        if(ProxyServer.getInstance().getServersCopy().containsKey(e.getServerName()) && e.getServerType().equals(ServerType.GAME_SERVER))
            ProxyServer.getInstance().getServers().remove(e.getServerName());
    }
}
