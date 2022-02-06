/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.wrapper.proxy;

import de.verdox.vcore.plugin.wrapper.Platform;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 04.08.2021 23:42
 */
public interface ProxyPlatform extends Platform {
    boolean sendToServer(@NotNull UUID playerUUID, @NotNull String serverName);

    boolean kickPlayer(@NotNull UUID playerUUID, @NotNull String kickMessage);
}
