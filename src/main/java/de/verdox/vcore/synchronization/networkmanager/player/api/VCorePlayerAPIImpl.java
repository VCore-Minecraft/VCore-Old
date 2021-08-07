/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.api;

import de.verdox.vcore.plugin.SystemLoadable;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.wrapper.types.enums.PlayerMessageType;
import de.verdox.vcore.synchronization.messaging.MessagingService;
import de.verdox.vcore.synchronization.messaging.instructions.InstructionService;
import de.verdox.vcore.synchronization.networkmanager.enums.GlobalProperty;
import de.verdox.vcore.plugin.wrapper.types.enums.PlayerGameMode;
import de.verdox.vcore.synchronization.networkmanager.player.VCorePlayer;
import de.verdox.vcore.synchronization.networkmanager.player.api.instructions.queries.QueryPlayerPosition;
import de.verdox.vcore.synchronization.networkmanager.player.api.instructions.updates.*;
import de.verdox.vcore.plugin.wrapper.types.ServerLocation;
import de.verdox.vcore.synchronization.networkmanager.player.scheduling.VCorePlayerTaskScheduler;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.*;

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

    public VCorePlayerAPIImpl(VCorePlugin<?,?> plugin){
        this.plugin = plugin;
        this.messagingService = plugin.getServices().getMessagingService();
        this.instructionService = messagingService.getInstructionService();
        this.vCorePlayerTaskScheduler = new VCorePlayerTaskScheduler(plugin);
        registerStandardInstructions();
    }

    private void registerStandardInstructions(){
        plugin.getServices().getMessagingService().getInstructionService().registerInstructionType(0, QueryPlayerPosition.class);
        plugin.getServices().getMessagingService().getInstructionService().registerInstructionType(1, UpdatePlayerPosition.class);
        plugin.getServices().getMessagingService().getInstructionService().registerInstructionType(2, UpdatePlayerKick.class);
        plugin.getServices().getMessagingService().getInstructionService().registerInstructionType(3, UpdatePlayerServer.class);
        plugin.getServices().getMessagingService().getInstructionService().registerInstructionType(4, UpdatePlayerSendMessage.class);
        plugin.getServices().getMessagingService().getInstructionService().registerInstructionType(5, UpdatePlayerHealth.class);
        plugin.getServices().getMessagingService().getInstructionService().registerInstructionType(6, UpdatePlayerFood.class);
        plugin.getServices().getMessagingService().getInstructionService().registerInstructionType(7, UpdatePlayerGameMode.class);
        plugin.getServices().getMessagingService().getInstructionService().registerInstructionType(8, UpdateBroadcastMessage.class);
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
    public CompletableFuture<VCorePlayer> getVCorePlayer(@Nonnull UUID uuid) {
        CompletableFuture<VCorePlayer> future = new CompletableFuture<>();
        plugin.async(() -> future.complete(plugin.getServices().getPipeline().load(VCorePlayer.class,uuid, Pipeline.LoadingStrategy.LOAD_PIPELINE,false)));
        return future;
    }

    @Override
    public VCorePlayerTaskScheduler getPlayerScheduler() {
        return this.vCorePlayerTaskScheduler;
    }

    @Override
    public CompletableFuture<Boolean> isOnline(@Nonnull UUID uuid) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        plugin.async(() -> completableFuture.complete(plugin.getServices().getPipeline().exist(VCorePlayer.class,uuid, Pipeline.QueryStrategy.LOCAL, Pipeline.QueryStrategy.GLOBAL_CACHE)));
        return completableFuture;
    }

    @Override
    public CompletableFuture<VCorePlayer> getVCorePlayer(@Nonnull String userName) {
        CompletableFuture<VCorePlayer> future = new CompletableFuture<>();
        plugin.async(() -> {
            VCorePlayer foundPlayer = plugin.getServices().getPipeline().loadAllData(VCorePlayer.class, Pipeline.LoadingStrategy.LOAD_PIPELINE)
                    .stream()
                    .filter(vCorePlayer -> vCorePlayer.getDisplayName().equalsIgnoreCase(userName)).findAny().orElse(null);
            future.complete(foundPlayer);
        });
        return future;
    }

    @Override
    public CompletableFuture<Set<VCorePlayer>> getAllOnlinePlayers() {
        return plugin.getServices().getPipeline().loadAllDataAsync(VCorePlayer.class, Pipeline.LoadingStrategy.LOAD_PIPELINE);
    }

    @Override
    public CompletableFuture<ServerLocation> getServerLocation(@Nonnull VCorePlayer vCorePlayer) {
        QueryPlayerPosition queryPlayerPosition = new QueryPlayerPosition(UUID.randomUUID());
        queryPlayerPosition.withData(vCorePlayer.getObjectUUID());
        plugin.getServices().getMessagingService().getInstructionService().sendInstruction(queryPlayerPosition);
        return queryPlayerPosition.getFuture();
    }

    @Override
    public CompletableFuture<String> getPlayerIP(@Nonnull VCorePlayer vCorePlayer) {
        return null;
    }

    @Override
    public void teleport(@Nonnull VCorePlayer vCorePlayer, @Nonnull ServerLocation serverLocation) {
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
    public void teleport(@Nonnull VCorePlayer vCorePlayer, @Nonnull VCorePlayer target) {
        plugin.async(() -> {
            try {
                ServerLocation serverLocation = getServerLocation(target).get(5,TimeUnit.SECONDS);
                if(serverLocation == null)
                    return;
                teleport(vCorePlayer,serverLocation);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                sendMessage(vCorePlayer,PlayerMessageType.CHAT,"&cTarget location could not be found&7!");
            }
        });
    }

    @Override
    public void kickPlayer(@Nonnull VCorePlayer vCorePlayer, @Nonnull String message) {
        UpdatePlayerKick updatePlayerKick = new UpdatePlayerKick(UUID.randomUUID());
        updatePlayerKick.withData(vCorePlayer.getObjectUUID(),message);
        plugin.getServices().getMessagingService().getInstructionService().sendInstruction(updatePlayerKick);
    }

    @Override
    public void changeServer(@Nonnull VCorePlayer vCorePlayer, @Nonnull String serverName) {
        UpdatePlayerServer updatePlayerServer = new UpdatePlayerServer(UUID.randomUUID());
        updatePlayerServer.withData(vCorePlayer.getObjectUUID(),serverName);
        plugin.getServices().getMessagingService().getInstructionService().sendInstruction(updatePlayerServer);
    }

    @Override
    public void sendMessage(@Nonnull VCorePlayer vCorePlayer, @Nonnull PlayerMessageType playerMessageType, @Nonnull String message) {
        UpdatePlayerSendMessage updatePlayerSendMessage = new UpdatePlayerSendMessage(UUID.randomUUID());
        updatePlayerSendMessage.withData(vCorePlayer.getObjectUUID(),playerMessageType.name(),message);
        plugin.getServices().getMessagingService().getInstructionService().sendInstruction(updatePlayerSendMessage);
    }

    @Override
    public void healPlayer(@Nonnull VCorePlayer vCorePlayer) {
        UpdatePlayerHealth updatePlayerHealth = new UpdatePlayerHealth(UUID.randomUUID());
        updatePlayerHealth.withData(vCorePlayer.getObjectUUID(),20d);
        plugin.getServices().getMessagingService().getInstructionService().sendInstruction(updatePlayerHealth);
    }

    @Override
    public void feedPlayer(@Nonnull VCorePlayer vCorePlayer) {
        UpdatePlayerFood updatePlayerFood = new UpdatePlayerFood(UUID.randomUUID());
        updatePlayerFood.withData(vCorePlayer.getObjectUUID(),10);
        plugin.getServices().getMessagingService().getInstructionService().sendInstruction(updatePlayerFood);
    }

    @Override
    public void setGameMode(@Nonnull VCorePlayer vCorePlayer, @Nonnull PlayerGameMode gameMode) {
        UpdatePlayerGameMode updatePlayerGameMode = new UpdatePlayerGameMode(UUID.randomUUID());
        updatePlayerGameMode.withData(vCorePlayer.getObjectUUID(), gameMode.name());
        plugin.getServices().getMessagingService().getInstructionService().sendInstruction(updatePlayerGameMode);
    }

    //TODO: Global Property wird bislang nicht genutzt / Evtl mit Servernamen austauschen?
    @Override
    public void broadcastMessage(@Nonnull String message, @Nonnull PlayerMessageType playerMessageType, @Nonnull GlobalProperty globalProperty) {
        UpdateBroadcastMessage updateBroadcastMessage = new UpdateBroadcastMessage(UUID.randomUUID());
        updateBroadcastMessage.withData(playerMessageType.name(),message);
        plugin.getServices().getMessagingService().getInstructionService().sendInstruction(updateBroadcastMessage);
    }
}
