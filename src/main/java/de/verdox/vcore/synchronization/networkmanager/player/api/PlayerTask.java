/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.api;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.08.2021 15:00
 */
public final class PlayerTask {
    private final UUID uuid;
    private final UUID taskUUID;
    private final Runnable runnable;

    public PlayerTask(@Nonnull UUID uuid, @NotNull UUID taskUUID, @Nonnull Runnable runnable) {
        this.uuid = uuid;
        this.taskUUID = taskUUID;
        this.runnable = runnable;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public UUID getTaskUUID() {
        return taskUUID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerTask)) return false;
        PlayerTask that = (PlayerTask) o;
        return Objects.equals(getTaskUUID(), that.getTaskUUID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTaskUUID());
    }
}
