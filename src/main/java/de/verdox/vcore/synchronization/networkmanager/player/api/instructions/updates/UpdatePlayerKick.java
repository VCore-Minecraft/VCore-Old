/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.api.instructions.updates;

import de.verdox.vcore.synchronization.messaging.instructions.update.Update;
import de.verdox.vcore.synchronization.networkmanager.player.api.VCorePlayerAPI;

import java.util.List;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.08.2021 22:27
 */
public class UpdatePlayerKick extends Update {
    public UpdatePlayerKick(UUID uuid) {
        super(uuid);
    }

    @Override
    public Object[] respondToInstruction(Object[] instructionData) {
        UUID targetUUID = (UUID) instructionData[0];
        String kickMessage = (String) instructionData[1];
        if(kickMessage == null)
            kickMessage = "&cYou were kicked from the network&7!";
        if(bungeePlatform != null){
            bungeePlatform.kickPlayer(targetUUID,kickMessage);
        }
        else if(spigotPlatform != null){
            String finalKickMessage = kickMessage;
            plugin.sync(() -> spigotPlatform.kickPlayer(targetUUID, finalKickMessage));
        }
        return new Object[0];
    }

    @Override
    protected List<Class<?>> dataTypes() {
        return List.of(UUID.class, String.class);
    }

    @Override
    protected List<String> parameters() {
        return List.of(VCorePlayerAPI.APIParameters.UPDATE_PLAYER_KICK.name());
    }
}
