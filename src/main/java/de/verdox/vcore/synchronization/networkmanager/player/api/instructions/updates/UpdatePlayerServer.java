/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.api.instructions.updates;

import de.verdox.vcore.synchronization.messaging.instructions.update.CleverUpdate;
import de.verdox.vcore.synchronization.networkmanager.player.api.VCorePlayerAPI;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.08.2021 22:27
 */
public class UpdatePlayerServer extends CleverUpdate {
    public UpdatePlayerServer(UUID uuid) {
        super(uuid);
    }

    @NotNull
    @Override
    public UpdateCompletion executeUpdate(Object[] instructionData) {

        UUID targetUUID = (UUID) instructionData[0];
        String serverName = (String) instructionData[1];

        if (!checkOnlineOnBungeeCord(targetUUID))
            return UpdateCompletion.NOTHING;

        if (serverName.equals(plugin.getCoreInstance().getServerName()))
            return UpdateCompletion.FALSE;
        proxyPlatform.sendToServer(targetUUID, serverName);
        return UpdateCompletion.TRUE;
    }

    @Override
    protected List<Class<?>> dataTypes() {
        return List.of(UUID.class, String.class);
    }

    @Override
    protected List<String> parameters() {
        return List.of(VCorePlayerAPI.APIParameters.UPDATE_PLAYER_SERVER.name());
    }
}
