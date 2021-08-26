/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.wrapper;

import de.verdox.vcore.plugin.wrapper.bungeecord.BungeePlatform;
import de.verdox.vcore.plugin.wrapper.spigot.SpigotPlatform;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 01.08.2021 20:00
 */
public interface PlatformWrapper {
    boolean isPlayerOnline(@Nonnull UUID playerUUID);

    boolean isPrimaryThread();

    void shutdown();

    InetSocketAddress getPlayerAddress(@Nonnull UUID playerUUID);

    SpigotPlatform getSpigotPlatform();

    BungeePlatform getBungeePlatform();
}
