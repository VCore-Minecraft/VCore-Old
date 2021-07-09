/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.pingservice;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 09.07.2021 00:29
 */
public class ServerInstance {
    private final ServerType serverType;
    private final String serverName;
    private final String serverAddress;
    private final int serverPort;

    public ServerInstance(ServerType serverType, String serverName, String serverAddress, int serverPort){
        this.serverType = serverType;
        this.serverName = serverName;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getServerName() {
        return serverName;
    }
}
