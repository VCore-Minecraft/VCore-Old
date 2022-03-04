/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.wrapper.gameserver;

import de.verdox.vcore.plugin.wrapper.Platform;
import de.verdox.vcore.plugin.wrapper.types.GameLocation;
import de.verdox.vcore.plugin.wrapper.types.enums.PlayerGameMode;
import de.verdox.vcore.plugin.wrapper.types.enums.PlayerMessageType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 04.08.2021 23:42
 */
public interface GameServerPlatform extends Platform {

    void setPlayerHealth(@NotNull UUID playerUUID, double health);

    void setPlayerFood(@NotNull UUID playerUUID, int food);

    void teleportPlayer(@NotNull UUID playerUUID, @NotNull GameLocation gameLocation);

    void setGameMode(@NotNull UUID playerUUID, @NotNull PlayerGameMode playerGameMode);

    void sendMessage(@NotNull UUID playerUUID, @NotNull String message, @NotNull PlayerMessageType playerMessageType);

    void broadcastMessage(@NotNull String message, @NotNull PlayerMessageType playerMessageType);

    void kickPlayer(@NotNull UUID playerUUID, @NotNull String kickMessage);

    void killPlayer(@NotNull UUID playerUUID);

    void clearInventory(@NotNull UUID playerUUID);

    GameLocation getLocation(@NotNull UUID playerUUID);

    double getTPS();

}
