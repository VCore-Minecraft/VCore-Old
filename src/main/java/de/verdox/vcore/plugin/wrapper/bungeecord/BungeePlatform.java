/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.wrapper.bungeecord;

import de.verdox.vcore.plugin.wrapper.Platform;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 04.08.2021 23:42
 */
public interface BungeePlatform extends Platform {
    boolean sendToServer(@Nonnull UUID playerUUID, @Nonnull String serverName);

    boolean kickPlayer(@Nonnull UUID playerUUID, @Nonnull String kickMessage);
}
