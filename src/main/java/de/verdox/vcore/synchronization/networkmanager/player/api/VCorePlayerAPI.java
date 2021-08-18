/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.api;

import de.verdox.vcore.plugin.wrapper.types.enums.PlayerMessageType;
import de.verdox.vcore.synchronization.networkmanager.enums.GlobalProperty;
import de.verdox.vcore.plugin.wrapper.types.enums.PlayerGameMode;
import de.verdox.vcore.synchronization.networkmanager.player.VCorePlayer;
import de.verdox.vcore.plugin.wrapper.types.ServerLocation;
import de.verdox.vcore.synchronization.networkmanager.player.scheduling.VCorePlayerTaskScheduler;

import javax.annotation.Nonnull;
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

    CompletableFuture<Boolean> isOnline(@Nonnull UUID uuid);
    CompletableFuture<VCorePlayer> getVCorePlayerAsync(@Nonnull UUID uuid);
    CompletableFuture<VCorePlayer> getVCorePlayerAsync(@Nonnull String userName);
    VCorePlayer getVCorePlayer(@Nonnull UUID uuid);
    VCorePlayer getVCorePlayer(@Nonnull String userName);

    CompletableFuture<Set<VCorePlayer>> getAllOnlinePlayers();

    CompletableFuture<ServerLocation> getServerLocation(@Nonnull VCorePlayer vCorePlayer);
    CompletableFuture<String> getPlayerIP(@Nonnull VCorePlayer vCorePlayer);

    //TODO: gamerules global ändern (für alle welten und nur eine welt (mehrere welten auf anderen servern mit gleichem server)), difficulty, sendTitle,
    //TODO: globales Scoreboard, globale BossBar, give, kill, IP Command, PlayerInfo Command mit allen verfügbaren Daten über den VCorePlayer

    //TODO: Update Functions auf Future Objekte ändern -> Checken obs geklappt hat -> Command Response für Sender
    void teleport(@Nonnull VCorePlayer vCorePlayer, @Nonnull ServerLocation serverLocation);
    void teleport(@Nonnull VCorePlayer vCorePlayer, @Nonnull VCorePlayer target);

    void kickPlayer(@Nonnull VCorePlayer vCorePlayer, @Nonnull String message);
    void changeServer(@Nonnull VCorePlayer vCorePlayer, @Nonnull String serverName);

    void sendMessage(@Nonnull VCorePlayer vCorePlayer, @Nonnull PlayerMessageType playerMessageType, @Nonnull String message);

    void healPlayer(@Nonnull VCorePlayer vCorePlayer);
    void feedPlayer(@Nonnull VCorePlayer vCorePlayer);
    void setGameMode(@Nonnull VCorePlayer vCorePlayer, @Nonnull PlayerGameMode gameMode);
    void clearInventory(@Nonnull VCorePlayer vCorePlayer);

    void broadcastMessage(@Nonnull String message, @Nonnull PlayerMessageType playerMessageType, @Nonnull GlobalProperty globalProperty);
    //TODO: Send message an Liste von Spielern
    //TODO: Globale Message, Serverglobale MEssage ( Auf dem GameServer oder Proxy Globale Message)



    enum APIParameters{
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

        APIParameters(String parameter){
            this.parameter = parameter;
        }

        public String getParameter() {
            return parameter;
        }
    }
}
