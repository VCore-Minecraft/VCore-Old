/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.serverping.events;

import de.verdox.vcore.synchronization.networkmanager.server.ServerType;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 09.07.2021 00:26
 */
public abstract class ServerPingEvent {
    private final ServerType serverType;
    private final String serverName;
    private final String serverAddress;
    private final int serverPort;

    public ServerPingEvent(ServerType serverType, String serverName, String serverAddress, int serverPort) {
        this.serverType = serverType;
        this.serverName = serverName.toLowerCase();
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public String getServerName() {
        return serverName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public String getServerAddress() {
        return serverAddress;
    }
}
