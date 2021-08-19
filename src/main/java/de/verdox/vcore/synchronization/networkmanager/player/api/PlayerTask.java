/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.api;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.08.2021 15:00
 */
public final class PlayerTask {
    private final UUID uuid;
    private final Runnable runnable;

    public PlayerTask(@Nonnull UUID uuid, @Nonnull Runnable runnable) {
        this.uuid = uuid;
        this.runnable = runnable;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Runnable getRunnable() {
        return runnable;
    }
}
