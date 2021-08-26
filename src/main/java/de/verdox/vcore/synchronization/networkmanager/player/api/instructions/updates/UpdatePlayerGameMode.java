/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.api.instructions.updates;

import de.verdox.vcore.plugin.wrapper.types.enums.PlayerGameMode;
import de.verdox.vcore.synchronization.messaging.instructions.update.CleverUpdate;
import de.verdox.vcore.synchronization.networkmanager.player.api.VCorePlayerAPI;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.08.2021 22:30
 */
public class UpdatePlayerGameMode extends CleverUpdate {
    public UpdatePlayerGameMode(UUID uuid) {
        super(uuid);
    }

    @NotNull
    @Override
    public UpdateCompletion executeUpdate(Object[] instructionData) {
        UUID target = (UUID) instructionData[0];
        String gameMode = (String) instructionData[1];

        if (!checkOnlineOnSpigot(target))
            return UpdateCompletion.NOTHING;

        PlayerGameMode playerGameMode = PlayerGameMode.valueOf(gameMode);
        plugin.sync(() -> spigotPlatform.setGameMode(target, playerGameMode));
        return UpdateCompletion.TRUE;
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
