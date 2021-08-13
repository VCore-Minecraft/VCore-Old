/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.api.instructions.updates;

import de.verdox.vcore.plugin.wrapper.types.enums.PlayerMessageType;
import de.verdox.vcore.synchronization.messaging.instructions.update.Update;
import de.verdox.vcore.synchronization.networkmanager.player.api.VCorePlayerAPI;

import java.util.List;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.08.2021 22:30
 */
public class UpdateBroadcastMessage extends Update {
    public UpdateBroadcastMessage(UUID uuid) {
        super(uuid);
    }

    @Override
    public Object[] respondToInstruction(Object[] instructionData) {
        if(spigotPlatform != null){
            String messageType = (String) instructionData[0];
            String message = (String) instructionData[1];
            PlayerMessageType playerMessageType = PlayerMessageType.valueOf(messageType);
            spigotPlatform.broadcastMessage(message, playerMessageType);
        }
        return new Object[0];
    }

    @Override
    protected List<Class<?>> dataTypes() {
        return List.of(String.class, String.class);
    }

    @Override
    protected List<String> parameters() {
        return List.of(VCorePlayerAPI.APIParameters.UPDATE_BROADCASTMESSAGE.name());
    }
}