/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging.instructions;

import java.util.concurrent.CompletableFuture;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 26.08.2021 02:12
 */
public interface InstructionSender<T> {
    boolean onSend(CompletableFuture<T> future, Object[] queryData);

    CompletableFuture<T> getFuture();
}
