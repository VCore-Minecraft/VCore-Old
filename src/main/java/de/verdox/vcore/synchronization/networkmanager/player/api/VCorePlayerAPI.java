/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.api;

import de.verdox.vcore.plugin.wrapper.types.ServerLocation;
import de.verdox.vcore.plugin.wrapper.types.enums.PlayerGameMode;
import de.verdox.vcore.plugin.wrapper.types.enums.PlayerMessageType;
import de.verdox.vcore.synchronization.networkmanager.enums.GlobalProperty;
import de.verdox.vcore.synchronization.networkmanager.player.VCorePlayer;
import de.verdox.vcore.synchronization.networkmanager.player.scheduling.VCorePlayerTaskScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 03.08.2021 19:55
 */
public interface VCorePlayerAPI {
    VCorePlayerTaskScheduler getPlayerScheduler();

    CompletableFuture<Boolean> isOnline(@NotNull UUID uuid);

    CompletableFuture<VCorePlayer> getVCorePlayerAsync(@NotNull UUID uuid);

    CompletableFuture<VCorePlayer> getVCorePlayerAsync(@NotNull String userName);

    VCorePlayer getVCorePlayer(@NotNull UUID uuid);

    VCorePlayer getVCorePlayer(@NotNull String userName);

    CompletableFuture<Set<VCorePlayer>> getAllOnlinePlayers();

    CompletableFuture<ServerLocation> getServerLocation(@NotNull VCorePlayer vCorePlayer);

    CompletableFuture<String> getPlayerIP(@NotNull VCorePlayer vCorePlayer);

    //TODO: gamerules global ändern (für alle welten und nur eine welt (mehrere welten auf anderen servern mit gleichem server)), difficulty, sendTitle,
    //TODO: globales Scoreboard, globale BossBar, give, kill, IP Command, PlayerInfo Command mit allen verfügbaren Daten über den VCorePlayer

    //TODO: Update Functions auf Future Objekte ändern -> Checken obs geklappt hat -> Command Response für Sender
    void teleport(@NotNull VCorePlayer vCorePlayer, @NotNull ServerLocation serverLocation);

    void teleport(@NotNull VCorePlayer vCorePlayer, @NotNull VCorePlayer target);

    void kickPlayer(@NotNull VCorePlayer vCorePlayer, @NotNull String message);

    void changeServer(@NotNull VCorePlayer vCorePlayer, @NotNull String serverName);

    void sendMessage(@NotNull VCorePlayer vCorePlayer, @NotNull PlayerMessageType playerMessageType, @NotNull String message);

    void healPlayer(@NotNull VCorePlayer vCorePlayer);

    void feedPlayer(@NotNull VCorePlayer vCorePlayer);

    void setGameMode(@NotNull VCorePlayer vCorePlayer, @NotNull PlayerGameMode gameMode);

    void clearInventory(@NotNull VCorePlayer vCorePlayer);

    void broadcastMessage(@NotNull String message, @NotNull PlayerMessageType playerMessageType, @NotNull GlobalProperty globalProperty);
    //TODO: Send message an Liste von Spielern
    //TODO: Globale Message, Serverglobale MEssage ( Auf dem GameServer oder Proxy Globale Message)


    enum APIParameters {
        QUERY_PLAYER_POSITION("QueryPlayerPosition"),
        QUERY_PLAYER_INVSEE("QueryPlayerInvsee"),

        UPDATE_PLAYER_POSITION("UpdatePlayerPosition"),
        UPDATE_PLAYER_KICK("UpdatePlayerKick"),
        UPDATE_PLAYER_SERVER("UpdatePlayerServer"),
        UPDATE_PLAYER_SENDMESSAGE("UpdatePlayerSendMessage"),
        UPDATE_PLAYER_HEALTH("UpdatePlayerHeal"),
        UPDATE_PLAYER_FOOD("UpdatePlayerFood"),
        UPDATE_PLAYER_GAMEMODE("UpdatePlayerGameMode"),
        UPDATE_BROADCASTMESSAGE("UpdateBroadcastMessage"),
        UPDATE_Player_CLEARINV("UpdatePlayerClearInv"),
        ;
        private final String parameter;

        APIParameters(String parameter) {
            this.parameter = parameter;
        }

        public String getParameter() {
            return parameter;
        }
    }
}
