/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.player.events;

import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 20.06.2021 00:52
 */
public class PlayerPreSessionLoadEvent {
    private final UUID playerUUID;

    public PlayerPreSessionLoadEvent(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }
}
