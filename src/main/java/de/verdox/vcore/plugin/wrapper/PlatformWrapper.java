/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.wrapper;

import de.verdox.vcore.plugin.wrapper.proxy.ProxyPlatform;
import de.verdox.vcore.plugin.wrapper.gameserver.GameServerPlatform;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 01.08.2021 20:00
 */
public interface PlatformWrapper {
    boolean isPlayerOnline(@NotNull UUID playerUUID);

    boolean isPrimaryThread();

    void shutdown();

    InetSocketAddress getPlayerAddress(@NotNull UUID playerUUID);

    GameServerPlatform getGameServerPlatform();

    ProxyPlatform getProxyPlatform();
}
