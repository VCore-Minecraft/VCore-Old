/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.api.instructions.updates;

import de.verdox.vcore.plugin.wrapper.bungeecord.BungeePlatform;
import de.verdox.vcore.plugin.wrapper.spigot.SpigotPlatform;
import de.verdox.vcore.plugin.wrapper.types.GameLocation;
import de.verdox.vcore.synchronization.messaging.instructions.update.Update;
import de.verdox.vcore.synchronization.networkmanager.player.VCorePlayer;
import de.verdox.vcore.synchronization.networkmanager.player.api.VCorePlayerAPI;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.08.2021 03:29
 */
public class UpdatePlayerPosition extends Update {
    public UpdatePlayerPosition(UUID uuid) {
        super(uuid);
    }

    @Nonnull
    @Override
    public UpdateCompletion executeUpdate(Object[] instructionData) {

        UUID uuid = (UUID) instructionData[0];
        VCorePlayer vCorePlayer = plugin.getServices().getPipeline().load(VCorePlayer.class,uuid, Pipeline.LoadingStrategy.LOAD_PIPELINE);
        if(vCorePlayer == null)
            return UpdateCompletion.NOTHING;

        String serverName = (String) instructionData[1];
        String worldName = (String) instructionData[2];
        double x = (double) instructionData[3];
        double y = (double) instructionData[4];
        double z = (double) instructionData[5];

        BungeePlatform bungeePlatform = plugin.getPlatformWrapper().getBungeePlatform();
        SpigotPlatform spigotPlatform = plugin.getPlatformWrapper().getSpigotPlatform();

        // If is BungeeCord
        if(bungeePlatform != null){
            if(vCorePlayer.currentGameServer.equals(serverName))
                return UpdateCompletion.NOTHING;
            bungeePlatform.sendToServer(vCorePlayer.getObjectUUID(),serverName);
        }
        else if(spigotPlatform != null){
            String gameServerName = plugin.getCoreInstance().getServerName();
            if(!serverName.equals(gameServerName))
                return UpdateCompletion.NOTHING;
            // Player is already online
            plugin.getCoreInstance().getPlayerAPI().getPlayerScheduler().schedulePlayerTask(uuid,() -> {
                GameLocation gameLocation = new GameLocation(worldName,x,y,z);
                spigotPlatform.teleportPlayer(vCorePlayer.getObjectUUID(),gameLocation);
            },5, TimeUnit.SECONDS);
        }
        return UpdateCompletion.TRUE;
    }

    @Override
    public List<String> parameters() {
        return List.of(VCorePlayerAPI.APIParameters.UPDATE_PLAYER_POSITION.name());
    }

    @Override
    protected List<Class<?>> dataTypes() {
        return List.of(UUID.class,String.class,String.class,Double.class,Double.class,Double.class);
    }

    @Override
    public boolean onSend(Object[] queryData) {
        return true;
    }
}
