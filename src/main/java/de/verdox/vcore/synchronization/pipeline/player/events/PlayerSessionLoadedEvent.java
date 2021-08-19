/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.player.events;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 20.06.2021 00:48
 */
public class PlayerSessionLoadedEvent {
    private final UUID playerUUID;
    private final long timeStamp;

    public PlayerSessionLoadedEvent(@Nonnull UUID playerUUID, long timeStamp) {
        this.playerUUID = playerUUID;
        this.timeStamp = timeStamp;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }
}
