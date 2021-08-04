/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.api;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.messaging.MessagingService;
import de.verdox.vcore.synchronization.messaging.query.QueryHandler;
import de.verdox.vcore.synchronization.messaging.query.QueryService;
import de.verdox.vcore.synchronization.networkmanager.player.VCorePlayer;
import de.verdox.vcore.synchronization.networkmanager.player.api.querytypes.ServerLocation;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 03.08.2021 20:12
 */
public abstract class VCorePlayerAPIImpl implements VCorePlayerAPI, QueryHandler {

    protected final VCorePlugin<?, ?> plugin;
    private final MessagingService<?> messagingService;
    private final QueryService queryService;
    protected final Map<UUID,CompletableFuture<ServerLocation>> locationQueryCache = new ConcurrentHashMap<>();

    public VCorePlayerAPIImpl(VCorePlugin<?,?> plugin){
        this.plugin = plugin;
        this.messagingService = plugin.getServices().getMessagingService();
        this.queryService = messagingService.getQueryService();
        this.queryService.registerHandler(this);
    }

    // PlayerAPI Part

    @Override
    public CompletableFuture<VCorePlayer> getVCorePlayer(@Nonnull UUID uuid) {
        CompletableFuture<VCorePlayer> future = new CompletableFuture<>();
        plugin.async(() -> future.complete(plugin.getServices().getPipeline().load(VCorePlayer.class,uuid, Pipeline.LoadingStrategy.LOAD_PIPELINE,false)));
        return future;
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
                sendMessage(vCorePlayer,"&cTarget location could not be found&7!");
            }
        });
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
    public CompletableFuture<List<VCorePlayer>> getAllOnlinePlayers() {
        CompletableFuture<List<VCorePlayer>> completableFuture = new CompletableFuture<>();
        plugin.async(() -> {
            completableFuture.complete(new ArrayList<>(plugin.getServices().getPipeline().loadAllData(VCorePlayer.class, Pipeline.LoadingStrategy.LOAD_PIPELINE)));
        });
        return completableFuture;
    }

    @Override
    public CompletableFuture<ServerLocation> getServerLocation(@Nonnull VCorePlayer vCorePlayer) {
        CompletableFuture<ServerLocation> completableFuture = new CompletableFuture<>();
        plugin.async(() -> {
            UUID queryUUID = queryService.sendQuery(messagingService.constructMessage()
                    .withParameters(APIParameters.QUERY.getParameter(), APIParameters.PLAYER_LOCATION_QUERY.getParameter())
                    .withData(vCorePlayer.getObjectUUID()).constructMessage());
            locationQueryCache.put(queryUUID,completableFuture);
        });
        return completableFuture;
    }

    @Override
    public void teleport(@Nonnull VCorePlayer vCorePlayer, @Nonnull ServerLocation serverLocation) {
        queryService.sendQuery(messagingService.constructMessage()
                .withParameters(APIParameters.QUERY.getParameter(),APIParameters.PLAYER_TELEPORT.getParameter())
                .withData(vCorePlayer.getObjectUUID(),
                        serverLocation.serverName,
                        serverLocation.worldName,
                        serverLocation.x,
                        serverLocation.y,
                        serverLocation.z
                ).constructMessage());
    }

    @Override
    public void kickPlayer(@Nonnull VCorePlayer vCorePlayer, @Nonnull String message) {
        queryService.sendQuery(messagingService.constructMessage()
                .withParameters(APIParameters.QUERY.getParameter(),APIParameters.PLAYER_KICK.getParameter())
                .withData(vCorePlayer.getObjectUUID(),
                        message
                ).constructMessage());
    }

    @Override
    public void sendMessage(@Nonnull VCorePlayer vCorePlayer, @Nonnull String message) {
        queryService.sendQuery(messagingService.constructMessage()
                .withParameters(APIParameters.QUERY.getParameter(),APIParameters.PLAYER_SEND_MESSAGE.getParameter())
                .withData(vCorePlayer.getObjectUUID(),
                        message
                ).constructMessage());
    }

    @Override
    public void changeServer(@Nonnull VCorePlayer vCorePlayer, @Nonnull String serverName) {
        queryService.sendQuery(messagingService.constructMessage()
                .withParameters(APIParameters.QUERY.getParameter(),APIParameters.PLAYER_SERVER_CHANGE.getParameter())
                .withData(vCorePlayer.getObjectUUID(),
                        serverName
                ).constructMessage());
    }

    @Override
    public void onQuerySend(UUID queryUUID, String[] parameters, Object[] queryData) { }
}
