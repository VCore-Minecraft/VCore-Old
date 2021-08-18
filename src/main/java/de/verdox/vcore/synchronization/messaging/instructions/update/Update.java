/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging.instructions.update;

import de.verdox.vcore.synchronization.messaging.instructions.InstructionResponder;
import de.verdox.vcore.synchronization.messaging.instructions.MessagingInstruction;
import de.verdox.vcore.synchronization.messaging.instructions.annotations.InstructionInfo;
import de.verdox.vcore.synchronization.messaging.instructions.query.Query;

import javax.annotation.Nonnull;
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

    @Nonnull
    protected abstract UpdateCompletion executeUpdate(Object[] instructionData);

    @Override
    public boolean onSend(Object[] instructionData) {
        return true;
    }

    @Override
    public final void onResponse(CompletableFuture<Boolean> future, Object[] queryData, Object[] responseData) {
        future.complete((Boolean) responseData[0]);
    }

    @Override
    public final Object[] respondToInstruction(Object[] instructionData) {
        UpdateCompletion updateCompletion = executeUpdate(instructionData);
        switch (updateCompletion){
            case TRUE: return new Object[]{true};
            case FALSE: return new Object[]{false};
            default: return null;
        }
    }

    public enum UpdateCompletion {
        TRUE,
        FALSE,
        NOTHING,
    }
}
