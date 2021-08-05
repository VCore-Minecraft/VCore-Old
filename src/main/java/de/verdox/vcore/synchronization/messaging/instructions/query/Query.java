/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging.instructions.query;

import de.verdox.vcore.synchronization.messaging.instructions.InstructionResponder;
import de.verdox.vcore.synchronization.messaging.instructions.MessagingInstruction;
import de.verdox.vcore.synchronization.messaging.instructions.ResponseProcessor;
import de.verdox.vcore.synchronization.messaging.instructions.annotations.InstructionInfo;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.08.2021 02:16
 */

@InstructionInfo(awaitsResponse = true)
public abstract class Query<T> extends MessagingInstruction implements InstructionResponder, ResponseProcessor<T> {
    private final CompletableFuture<T> future = new CompletableFuture<>();
    public Query(UUID uuid){
        super(uuid);
    }

    @Override
    public CompletableFuture<T> getFuture() {
        return future;
    }

    @Override
    public boolean onSend(Object[] instructionData) {
        return true;
    }
}
