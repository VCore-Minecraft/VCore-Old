/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging.instructions.update;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 26.08.2021 02:29
 */

/**
 * A CleverUpdate is an Update that first executes the executeUpdate function on its own.
 * If the Function returns UpdateComplection.TRUE it will cancel the instruction as it has been completed successfully on its on platform
 * Else it will send the instruction to the network but will not answer it itself.
 */
public abstract class CleverUpdate extends Update {
    public CleverUpdate(UUID uuid) {
        super(uuid);
    }

    @Override
    protected final boolean onSend(Object[] instructionData, CompletableFuture<Boolean> future) {
        return !executeUpdate(instructionData).toValue();
    }

    @Override
    public final boolean respondToItself() {
        return false;
    }
}
