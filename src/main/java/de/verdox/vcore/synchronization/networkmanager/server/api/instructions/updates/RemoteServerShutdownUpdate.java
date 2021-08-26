/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.server.api.instructions.updates;

import de.verdox.vcore.synchronization.messaging.instructions.update.CleverUpdate;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 26.08.2021 20:41
 */
public class RemoteServerShutdownUpdate extends CleverUpdate {
    public RemoteServerShutdownUpdate(UUID uuid) {
        super(uuid);
    }

    @Override
    protected List<Class<?>> dataTypes() {
        return List.of(String.class, Boolean.class);
    }

    @Override
    protected List<String> parameters() {
        return List.of("ServerAPI", "RemoteShutdown");
    }

    @Nonnull
    @Override
    protected UpdateCompletion executeUpdate(Object[] instructionData) {
        String serverName = (String) instructionData[0];
        boolean ignoreSelf = (boolean) instructionData[1];

        if (!plugin.getCoreInstance().getServerName().equals(serverName))
            return UpdateCompletion.NOTHING;
        if (ignoreSelf)
            return UpdateCompletion.NOTHING;
        plugin.consoleMessage("&eReceived remote Shutdown Instruction", false);
        platformWrapper.shutdown();
        return UpdateCompletion.TRUE;
    }
}
