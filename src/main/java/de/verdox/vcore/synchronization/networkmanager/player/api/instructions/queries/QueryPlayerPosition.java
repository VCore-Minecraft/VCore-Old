/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.api.instructions.queries;

import de.verdox.vcore.plugin.wrapper.spigot.SpigotPlatform;
import de.verdox.vcore.plugin.wrapper.types.GameLocation;
import de.verdox.vcore.synchronization.messaging.instructions.query.Query;
import de.verdox.vcore.synchronization.networkmanager.player.VCorePlayer;
import de.verdox.vcore.synchronization.networkmanager.player.api.VCorePlayerAPI;
import de.verdox.vcore.plugin.wrapper.types.ServerLocation;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.08.2021 03:02
 */
public class QueryPlayerPosition extends Query<ServerLocation> {

    public QueryPlayerPosition(UUID uuid) {
        super(uuid);
    }

    @Override
    public List<String> parameters() {
        return List.of(VCorePlayerAPI.APIParameters.QUERY_PLAYER_POSITION.name());
    }

    @Override
    protected List<Class<?>> dataTypes() {
        return List.of(UUID.class);
    }

    @Override
    public boolean onSend(Object[] queryData) {
        return true;
    }

    @Override
    public void onResponse(CompletableFuture<ServerLocation> future, Object[] queryData, Object[] responseData) {
        ServerLocation serverLocation = new ServerLocation();
        serverLocation.serverName = (String) responseData[0];
        serverLocation.worldName = (String) responseData[1];
        serverLocation.x = (double) responseData[2];
        serverLocation.y = (double) responseData[3];
        serverLocation.z = (double) responseData[4];
        future.complete(serverLocation);
    }

    @Override
    public Object[] respondToInstruction(Object[] instructionData) {
        UUID playerUUID = (UUID) instructionData[0];
        SpigotPlatform spigotPlatform = plugin.getPlatformWrapper().getSpigotPlatform();
        // Makes sure it runs on Spigot Platform
        if(spigotPlatform == null)
            return null;
        VCorePlayer vCorePlayer = plugin.getServices().getPipeline().load(VCorePlayer.class,playerUUID, Pipeline.LoadingStrategy.LOAD_PIPELINE);
        if(vCorePlayer == null)
            return null;
        GameLocation gameLocation = spigotPlatform.getLocation(vCorePlayer.getObjectUUID());
        // Player is not online on this Server
        if(gameLocation == null)
            return null;
        return new Object[]{plugin.getCoreInstance().getServerName(),gameLocation.worldName,gameLocation.x,gameLocation.y,gameLocation.z};
    }
}
