/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.api.instructions.updates;

import de.verdox.vcore.plugin.wrapper.types.enums.PlayerMessageType;
import de.verdox.vcore.synchronization.messaging.instructions.update.Update;
import de.verdox.vcore.synchronization.networkmanager.player.api.VCorePlayerAPI;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.08.2021 22:29
 */
public class UpdatePlayerSendMessage extends Update {
    public UpdatePlayerSendMessage(UUID uuid) {
        super(uuid);
    }

    @Nonnull
    @Override
    public UpdateCompletion executeUpdate(Object[] instructionData) {

        UUID targetUUID = (UUID) instructionData[0];
        String messageType = (String) instructionData[1];
        String message = (String) instructionData[2];

        if(!checkOnlineOnSpigot(targetUUID))
            return UpdateCompletion.NOTHING;

        PlayerMessageType playerMessageType = PlayerMessageType.valueOf(messageType);
        spigotPlatform.sendMessage(targetUUID,message,playerMessageType);

        return UpdateCompletion.TRUE;
    }

    @Override
    protected List<Class<?>> dataTypes() {
        return List.of(UUID.class,String.class, String.class);
    }

    @Override
    protected List<String> parameters() {
        return List.of(VCorePlayerAPI.APIParameters.UPDATE_PLAYER_SENDMESSAGE.name());
    }
}
