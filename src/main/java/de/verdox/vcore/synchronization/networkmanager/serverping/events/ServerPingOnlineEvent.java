/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.serverping.events;

import de.verdox.vcore.synchronization.networkmanager.server.ServerType;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 09.07.2021 00:28
 */
public class ServerPingOnlineEvent extends ServerPingEvent {
    private final boolean firstReceivedPing;

    public ServerPingOnlineEvent(@NotNull ServerType serverType, @NotNull String serverName, @NotNull String serverAddress, int serverPort, boolean firstReceivedPing) {
        super(serverType, serverName, serverAddress, serverPort);
        this.firstReceivedPing = firstReceivedPing;
    }

    public boolean isFirstReceivedPing() {
        return firstReceivedPing;
    }
}
