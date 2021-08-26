/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging.instructions.update;

import de.verdox.vcore.synchronization.messaging.instructions.InstructionResponder;
import de.verdox.vcore.synchronization.messaging.instructions.annotations.InstructionInfo;
import de.verdox.vcore.synchronization.messaging.instructions.query.Query;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.08.2021 22:12
 */

@InstructionInfo(awaitsResponse = false)
public abstract class Update extends Query<Boolean> implements InstructionResponder {
    public Update(UUID uuid) {
        super(uuid);
    }

    @NotNull
    protected abstract UpdateCompletion executeUpdate(Object[] instructionData);

    protected boolean onSend(Object[] instructionData, CompletableFuture<Boolean> future) {
        return true;
    }

    @Override
    public final void onResponse(CompletableFuture<Boolean> future, Object[] queryData, Object[] responseData) {
        future.complete((Boolean) responseData[0]);
    }

    @Override
    public final Object[] respondToInstruction(Object[] instructionData) {
        UpdateCompletion updateCompletion = executeUpdate(instructionData);
        switch (updateCompletion) {
            case TRUE:
                return new Object[]{true};
            case FALSE:
                return new Object[]{false};
            default:
                return null;
        }
    }

    @Override
    public final boolean onSend(CompletableFuture<Boolean> future, Object[] queryData) {
        return onSend(queryData, future);
    }

    public enum UpdateCompletion {
        TRUE(true),
        FALSE(false),
        NOTHING(false),
        ;
        private final boolean value;

        UpdateCompletion(boolean value) {
            this.value = value;
        }

        public boolean toValue() {
            return value;
        }
    }
}
