/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.api;

import de.verdox.vcore.plugin.SystemLoadable;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.wrapper.types.ServerLocation;
import de.verdox.vcore.plugin.wrapper.types.enums.PlayerGameMode;
import de.verdox.vcore.plugin.wrapper.types.enums.PlayerMessageType;
import de.verdox.vcore.synchronization.messaging.MessagingService;
import de.verdox.vcore.synchronization.messaging.instructions.InstructionService;
import de.verdox.vcore.synchronization.networkmanager.enums.GlobalProperty;
import de.verdox.vcore.synchronization.networkmanager.player.VCorePlayer;
import de.verdox.vcore.synchronization.networkmanager.player.api.instructions.queries.QueryPlayerPosition;
import de.verdox.vcore.synchronization.networkmanager.player.api.instructions.updates.*;
import de.verdox.vcore.synchronization.networkmanager.player.scheduling.VCorePlayerTaskScheduler;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 03.08.2021 20:12
 */
public abstract class VCorePlayerAPIImpl implements VCorePlayerAPI, SystemLoadable {

    protected final VCorePlugin<?, ?> plugin;
    private final MessagingService<?> messagingService;
    private final InstructionService instructionService;
    protected VCorePlayerTaskScheduler vCorePlayerTaskScheduler;

    public VCorePlayerAPIImpl(@NotNull VCorePlugin<?, ?> plugin) {
        Objects.requireNonNull(plugin, "plugin can't be null!");
        this.plugin = plugin;
        this.messagingService = plugin.getServices().getMessagingService();
        this.instructionService = messagingService.getInstructionService();
        this.vCorePlayerTaskScheduler = new VCorePlayerTaskScheduler(plugin);
        registerStandardInstructions();
    }

    private void registerStandardInstructions() {
        instructionService.registerInstructionType(0, QueryPlayerPosition.class);
        instructionService.registerInstructionType(1, UpdatePlayerPosition.class);
        instructionService.registerInstructionType(2, UpdatePlayerKick.class);
        instructionService.registerInstructionType(3, UpdatePlayerServer.class);
        instructionService.registerInstructionType(4, UpdatePlayerSendMessage.class);
        instructionService.registerInstructionType(5, UpdatePlayerHealth.class);
        instructionService.registerInstructionType(6, UpdatePlayerFood.class);
        instructionService.registerInstructionType(7, UpdatePlayerGameMode.class);
        instructionService.registerInstructionType(8, UpdateBroadcastMessage.class);
        instructionService.registerInstructionType(9, UpdatePlayerClearInventory.class);

    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public void shutdown() {
        this.vCorePlayerTaskScheduler.shutdown();
    }

    // PlayerAPI Part


    @Override
    public VCorePlayer getVCorePlayer(@Nonnull @NotNull String userName) {
        return plugin.getServices().getPipeline().loadAllData(VCorePlayer.class, Pipeline.LoadingStrategy.LOAD_PIPELINE)
                .stream()
                .filter(vCorePlayer -> vCorePlayer.getDisplayName().equalsIgnoreCase(userName)).findAny().orElse(null);
    }

    @Override
    public VCorePlayer getVCorePlayer(@Nonnull @NotNull UUID uuid) {
        return plugin.getServices().getPipeline().load(VCorePlayer.class, uuid, Pipeline.LoadingStrategy.LOAD_PIPELINE, false);
    }

    @Override
    public CompletableFuture<VCorePlayer> getVCorePlayerAsync(@Nonnull @NotNull UUID uuid) {
        CompletableFuture<VCorePlayer> future = new CompletableFuture<>();
        plugin.async(() -> future.complete(getVCorePlayer(uuid)));
        return future;
    }

    @Override
    public CompletableFuture<VCorePlayer> getVCorePlayerAsync(@Nonnull @NotNull String userName) {
        CompletableFuture<VCorePlayer> future = new CompletableFuture<>();
        plugin.async(() -> future.complete(getVCorePlayer(userName)));
        return future;
    }

    @Override
    public VCorePlayerTaskScheduler getPlayerScheduler() {
        return this.vCorePlayerTaskScheduler;
    }

    @Override
    public CompletableFuture<Boolean> isOnline(@Nonnull @NotNull UUID uuid) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        plugin.async(() -> completableFuture.complete(plugin.getServices().getPipeline().exist(VCorePlayer.class, uuid, Pipeline.QueryStrategy.LOCAL, Pipeline.QueryStrategy.GLOBAL_CACHE)));
        return completableFuture;
    }

    @Override
    public CompletableFuture<Set<VCorePlayer>> getAllOnlinePlayers() {
        return plugin.getServices().getPipeline().loadAllDataAsync(VCorePlayer.class, Pipeline.LoadingStrategy.LOAD_PIPELINE);
    }

    @Override
    public CompletableFuture<ServerLocation> getServerLocation(@Nonnull @NotNull VCorePlayer vCorePlayer) {
        QueryPlayerPosition queryPlayerPosition = new QueryPlayerPosition(UUID.randomUUID());
        queryPlayerPosition.withData(vCorePlayer.getObjectUUID());
        instructionService.sendInstruction(queryPlayerPosition);
        return queryPlayerPosition.getFuture();
    }

    @Override
    public CompletableFuture<String> getPlayerIP(@Nonnull @NotNull VCorePlayer vCorePlayer) {
        return null;
    }

    @Override
    public void teleport(@Nonnull @NotNull VCorePlayer vCorePlayer, @Nonnull @NotNull ServerLocation serverLocation) {
        UpdatePlayerPosition updatePlayerPosition = new UpdatePlayerPosition(UUID.randomUUID());
        updatePlayerPosition
                .withData(vCorePlayer.getObjectUUID(),
                        serverLocation.serverName,
                        serverLocation.worldName,
                        serverLocation.x,
                        serverLocation.y,
                        serverLocation.z);
        plugin.getServices().getMessagingService().getInstructionService().sendInstruction(updatePlayerPosition);
    }

    @Override
    public void teleport(@Nonnull @NotNull VCorePlayer vCorePlayer, @Nonnull @NotNull VCorePlayer target) {
        plugin.async(() -> {
            try {
                ServerLocation serverLocation = getServerLocation(target).get(5, TimeUnit.SECONDS);
                if (serverLocation == null)
                    return;
                teleport(vCorePlayer, serverLocation);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                sendMessage(vCorePlayer, PlayerMessageType.CHAT, "&cTarget location could not be found&7!");
            }
        });
    }

    @Override
    public void kickPlayer(@Nonnull @NotNull VCorePlayer vCorePlayer, @Nonnull @NotNull String message) {
        UpdatePlayerKick updatePlayerKick = new UpdatePlayerKick(UUID.randomUUID());
        updatePlayerKick.withData(vCorePlayer.getObjectUUID(), message);
        instructionService.sendInstruction(updatePlayerKick);
    }

    @Override
    public void changeServer(@Nonnull @NotNull VCorePlayer vCorePlayer, @Nonnull @NotNull String serverName) {
        UpdatePlayerServer updatePlayerServer = new UpdatePlayerServer(UUID.randomUUID());
        updatePlayerServer.withData(vCorePlayer.getObjectUUID(), serverName);
        instructionService.sendInstruction(updatePlayerServer);
    }

    @Override
    public void sendMessage(@Nonnull @NotNull VCorePlayer vCorePlayer, @Nonnull @NotNull PlayerMessageType playerMessageType, @Nonnull @NotNull String message) {
        UpdatePlayerSendMessage updatePlayerSendMessage = new UpdatePlayerSendMessage(UUID.randomUUID());
        updatePlayerSendMessage.withData(vCorePlayer.getObjectUUID(), playerMessageType.name(), message);
        instructionService.sendInstruction(updatePlayerSendMessage);
    }

    @Override
    public void healPlayer(@Nonnull @NotNull VCorePlayer vCorePlayer) {
        UpdatePlayerHealth updatePlayerHealth = new UpdatePlayerHealth(UUID.randomUUID());
        updatePlayerHealth.withData(vCorePlayer.getObjectUUID(), 20d);
        instructionService.sendInstruction(updatePlayerHealth);
    }

    @Override
    public void feedPlayer(@Nonnull @NotNull VCorePlayer vCorePlayer) {
        UpdatePlayerFood updatePlayerFood = new UpdatePlayerFood(UUID.randomUUID());
        updatePlayerFood.withData(vCorePlayer.getObjectUUID(), 10);
        instructionService.sendInstruction(updatePlayerFood);
    }

    @Override
    public void setGameMode(@Nonnull @NotNull VCorePlayer vCorePlayer, @Nonnull @NotNull PlayerGameMode gameMode) {
        UpdatePlayerGameMode updatePlayerGameMode = new UpdatePlayerGameMode(UUID.randomUUID());
        updatePlayerGameMode.withData(vCorePlayer.getObjectUUID(), gameMode.name());
        instructionService.sendInstruction(updatePlayerGameMode);
    }

    //TODO: Global Property wird bislang nicht genutzt / Evtl mit Servernamen austauschen?
    @Override
    public void broadcastMessage(@Nonnull @NotNull String message, @Nonnull @NotNull PlayerMessageType playerMessageType, @Nonnull @NotNull GlobalProperty globalProperty) {
        UpdateBroadcastMessage updateBroadcastMessage = new UpdateBroadcastMessage(UUID.randomUUID());
        updateBroadcastMessage.withData(playerMessageType.name(), message);
        instructionService.sendInstruction(updateBroadcastMessage);
    }

    @Override
    public void clearInventory(@Nonnull @NotNull VCorePlayer vCorePlayer) {
        UpdatePlayerClearInventory updatePlayerClearInventory = new UpdatePlayerClearInventory(UUID.randomUUID());
        updatePlayerClearInventory.withData(vCorePlayer.getObjectUUID());
        instructionService.sendInstruction(updatePlayerClearInventory);
    }
}
