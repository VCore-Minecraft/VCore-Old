/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.api.instructions.updates;

import de.verdox.vcore.plugin.wrapper.types.enums.PlayerMessageType;
import de.verdox.vcore.synchronization.messaging.instructions.update.Update;
import de.verdox.vcore.synchronization.networkmanager.player.api.VCorePlayerAPI;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
    @Override
    public UpdateCompletion executeUpdate(Object[] instructionData) {
        if (gameServerPlatform == null)
            return UpdateCompletion.NOTHING;

        String messageType = (String) instructionData[0];
        String message = (String) instructionData[1];
        PlayerMessageType playerMessageType = PlayerMessageType.valueOf(messageType);
        gameServerPlatform.broadcastMessage(message, playerMessageType);

        return UpdateCompletion.TRUE;
    }


    @Override
    protected List<Class<?>> dataTypes() {
        return List.of(String.class, String.class);
    }

    @Override
    protected List<String> parameters() {
        return List.of(VCorePlayerAPI.APIParameters.UPDATE_BROADCASTMESSAGE.name());
    }

    @Override
    public boolean respondToItself() {
        return true;
    }
}
