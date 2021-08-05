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
public class UpdatePlayerHealth extends Update {
    public UpdatePlayerHealth(UUID uuid) {
        super(uuid);
    }

    @Override
    public Object[] respondToInstruction(Object[] instructionData) {
        UUID target = (UUID) instructionData[0];
        double health = (double) instructionData[1];

        if(spigotPlatform != null)
            plugin.sync(() -> spigotPlatform.setPlayerHealth(target,health));
        return new Object[0];
    }

    @Override
    protected List<Class<?>> dataTypes() {
        return List.of(UUID.class,Double.class);
    }

    @Override
    protected List<String> parameters() {
        return List.of(VCorePlayerAPI.APIParameters.UPDATE_PLAYER_HEALTH.name());
    }
}
