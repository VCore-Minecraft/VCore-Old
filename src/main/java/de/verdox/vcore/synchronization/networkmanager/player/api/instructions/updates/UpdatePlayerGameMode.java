/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.api.instructions.updates;

import de.verdox.vcore.plugin.wrapper.types.enums.PlayerGameMode;
import de.verdox.vcore.synchronization.messaging.instructions.update.Update;
import de.verdox.vcore.synchronization.networkmanager.player.api.VCorePlayerAPI;

import java.util.List;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.08.2021 22:30
 */
public class UpdatePlayerGameMode extends Update {
    public UpdatePlayerGameMode(UUID uuid) {
        super(uuid);
    }

    @Override
    public Object[] respondToInstruction(Object[] instructionData) {
        if(spigotPlatform != null){
            UUID target = (UUID) instructionData[0];
            String gameMode = (String) instructionData[1];
            PlayerGameMode playerGameMode = PlayerGameMode.valueOf(gameMode);
            plugin.sync(() -> spigotPlatform.setGameMode(target,playerGameMode));
        }
        return new Object[0];
    }

    @Override
    protected List<Class<?>> dataTypes() {
        return List.of(UUID.class, String.class);
    }

    @Override
    protected List<String> parameters() {
        return List.of(VCorePlayerAPI.APIParameters.UPDATE_PLAYER_GAMEMODE.name());
    }
}
