/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.api;

import de.verdox.vcore.synchronization.networkmanager.enums.GlobalProperty;
import de.verdox.vcore.synchronization.networkmanager.enums.PlayerGameMode;
import de.verdox.vcore.synchronization.networkmanager.player.VCorePlayer;
import de.verdox.vcore.synchronization.networkmanager.player.api.querytypes.ServerLocation;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 03.08.2021 19:55
 */
public interface VCorePlayerAPI {
    CompletableFuture<Boolean> isOnline(@Nonnull UUID uuid);
    CompletableFuture<VCorePlayer> getVCorePlayer(@Nonnull UUID uuid);
    CompletableFuture<VCorePlayer> getVCorePlayer(@Nonnull String userName);

    CompletableFuture<List<VCorePlayer>> getAllOnlinePlayers();

    CompletableFuture<ServerLocation> getServerLocation(@Nonnull VCorePlayer vCorePlayer);
    CompletableFuture<String> getPlayerIP(@Nonnull VCorePlayer vCorePlayer);

    //TODO: gamerules global ändern (für alle welten und nur eine welt (mehrere welten auf anderen servern mit gleichem server)), difficulty, sendTitle,
    //TODO: globales Scoreboard, globale BossBar, give, kill, IP Command, PlayerInfo Command mit allen verfügbaren Daten über den VCorePlayer

    void teleport(@Nonnull VCorePlayer vCorePlayer, @Nonnull ServerLocation serverLocation);
    void teleport(@Nonnull VCorePlayer vCorePlayer, @Nonnull VCorePlayer target);

    void kickPlayer(@Nonnull VCorePlayer vCorePlayer, @Nonnull String message);
    void changeServer(@Nonnull VCorePlayer vCorePlayer, @Nonnull String serverName);

    void sendMessage(@Nonnull VCorePlayer vCorePlayer, @Nonnull String message);
    void sendTitle(@Nonnull VCorePlayer vCorePlayer, @Nonnull String message);

    void healPlayer(@Nonnull VCorePlayer vCorePlayer);
    void feedPlayer(@Nonnull VCorePlayer vCorePlayer);
    void setGameMode(@Nonnull VCorePlayer vCorePlayer, @Nonnull PlayerGameMode gameMode);

    void broadcastMessage(@Nonnull String message, @Nonnull GlobalProperty globalProperty);
    //TODO: Send message an Liste von Spielern
    //TODO: Globale Message, Serverglobale MEssage ( Auf dem GameServer oder Proxy Globale Message)



    enum APIParameters{
        QUERY("vCorePlayerQuery"),
        PLAYER_LOCATION_QUERY("playerGetServerLocation"),
        PLAYER_TELEPORT("playerTeleport"),
        PLAYER_KICK("playerKick"),
        PLAYER_SERVER_CHANGE("playerChangeServer"),
        PLAYER_SEND_MESSAGE("playerSendMessage"),
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
