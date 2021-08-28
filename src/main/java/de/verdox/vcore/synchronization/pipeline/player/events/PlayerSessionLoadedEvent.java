/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.player.events;

import de.verdox.vcore.plugin.VCorePlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 20.06.2021 00:48
 */
public class PlayerSessionLoadedEvent {
    private final VCorePlugin<?, ?> plugin;
    private final UUID playerUUID;
    private final long timeStamp;

    public PlayerSessionLoadedEvent(@NotNull VCorePlugin<?, ?> plugin, @NotNull UUID playerUUID, long timeStamp) {
        this.plugin = plugin;
        this.playerUUID = playerUUID;
        this.timeStamp = timeStamp;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public VCorePlugin<?, ?> getPlugin() {
        return plugin;
    }
}
