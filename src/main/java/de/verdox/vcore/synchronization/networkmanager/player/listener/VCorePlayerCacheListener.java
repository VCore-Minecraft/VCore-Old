/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.listener;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.networkmanager.server.ServerType;
import de.verdox.vcore.synchronization.messaging.messages.Message;

import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 31.07.2021 21:40
 */
public interface VCorePlayerCacheListener {

    default void sendPlayerPing(VCorePlugin<?,?> plugin, String serverName, ServerType serverType, PlayerPingType playerPingType, UUID playerUUID, String playerName){
        Message message = plugin.getServices().getMessagingService().constructMessage()
                .withParameters("connection", playerPingType.name())
                .withData(serverType.name(), serverName, playerUUID, playerName)
                .constructMessage();
        plugin.getServices().getMessagingService().publishMessage(message);
    }

    enum PlayerPingType{
        JOIN,
        QUIT,
        KICK
    }
}
