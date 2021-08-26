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
 * @date 14.08.2021 23:30
 */
public class UpdatePlayerClearInventory extends CleverUpdate {
    public UpdatePlayerClearInventory(UUID uuid) {
        super(uuid);
    }

    @Override
    protected List<Class<?>> dataTypes() {
        return List.of(UUID.class);
    }

    @Override
    protected List<String> parameters() {
        return List.of(VCorePlayerAPI.APIParameters.UPDATE_Player_CLEARINV.name());
    }

    @NotNull
    @Override
    public UpdateCompletion executeUpdate(Object[] instructionData) {
        UUID target = (UUID) instructionData[0];

        if (!checkOnlineOnSpigot(target))
            return UpdateCompletion.NOTHING;
        plugin.sync(() -> spigotPlatform.clearInventory(target));
        return UpdateCompletion.TRUE;
    }
}
