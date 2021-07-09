/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.pingservice.events;

import de.verdox.vcore.plugin.pingservice.ServerType;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 09.07.2021 00:28
 */
public class ServerPingOnlineEvent extends ServerPingEvent{
    private boolean firstReceivedPing;

    public ServerPingOnlineEvent(ServerType serverType, String serverName, String serverAddress, int serverPort, boolean firstReceivedPing) {
        super(serverType, serverName, serverAddress, serverPort);
        this.firstReceivedPing = firstReceivedPing;
    }

    public boolean isFirstReceivedPing() {
        return firstReceivedPing;
    }
}
