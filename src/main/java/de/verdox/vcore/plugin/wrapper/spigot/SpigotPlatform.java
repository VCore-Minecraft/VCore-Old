/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.wrapper.spigot;

import de.verdox.vcore.plugin.wrapper.Platform;
import de.verdox.vcore.plugin.wrapper.types.enums.PlayerGameMode;
import de.verdox.vcore.plugin.wrapper.types.enums.PlayerMessageType;
import de.verdox.vcore.synchronization.networkmanager.player.VCorePlayer;
import de.verdox.vcore.plugin.wrapper.types.GameLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 04.08.2021 23:42
 */
public interface SpigotPlatform extends Platform {

    void setPlayerHealth(@Nonnull UUID playerUUID, double health);
    void setPlayerFood(@Nonnull UUID playerUUID, int food);
    void teleportPlayer(@Nonnull UUID playerUUID, @Nonnull GameLocation gameLocation);
    void setGameMode(@Nonnull UUID playerUUID, @Nonnull PlayerGameMode playerGameMode);
    void sendMessage(@Nonnull UUID playerUUID, @Nonnull String message, @Nonnull PlayerMessageType playerMessageType);
    void broadcastMessage(@Nonnull String message, @Nonnull PlayerMessageType playerMessageType);
    void kickPlayer(@Nonnull UUID playerUUID, @Nonnull String kickMessage);
    void killPlayer(@Nonnull UUID playerUUID);
    void clearInventory(@Nonnull UUID playerUUID);
    GameLocation getLocation(@Nonnull UUID playerUUID);

    double getTPS();

}
