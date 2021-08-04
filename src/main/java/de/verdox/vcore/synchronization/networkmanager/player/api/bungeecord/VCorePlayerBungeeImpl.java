/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.api.bungeecord;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.networkmanager.player.api.VCorePlayerAPIImpl;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;

import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 03.08.2021 23:23
 */
public class VCorePlayerBungeeImpl extends VCorePlayerAPIImpl implements Listener {
    public VCorePlayerBungeeImpl(VCorePlugin.BungeeCord plugin) {
        super(plugin);
        ProxyServer.getInstance().getPluginManager().registerListener(plugin,this);
    }

    @Override
    public Object[] respondToQuery(UUID queryUUID, String[] parameters, Object[] queryData) {
        if(!parameters[0].equals(APIParameters.QUERY.getParameter()))
            return null;
        if(parameters[1].equals(APIParameters.PLAYER_TELEPORT.getParameter())){
            UUID playerUUID = (UUID) queryData[0];
            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(playerUUID);
            if(proxiedPlayer == null)
                return null;
            String serverName = (String) queryData[1];
            ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(serverName);
            if(serverInfo == null){
                plugin.consoleMessage("&cError while connecting player &b"+proxiedPlayer.getDisplayName()+" &cto Server &e"+serverName,false);
                return null;
            }
            proxiedPlayer.connect(serverInfo, ServerConnectEvent.Reason.PLUGIN);
        }
        return null;
    }

    @Override
    public void onResponse(UUID queryUUID, String[] parameters, Object[] queryData, Object[] responseData) {

    }
}
