/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging.instructions.update;

import de.verdox.vcore.synchronization.messaging.instructions.InstructionResponder;
import de.verdox.vcore.synchronization.messaging.instructions.MessagingInstruction;
import de.verdox.vcore.synchronization.messaging.instructions.annotations.InstructionInfo;

import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.08.2021 22:12
 */

@InstructionInfo(awaitsResponse = true)
public abstract class Update extends MessagingInstruction implements InstructionResponder {
    public Update(UUID uuid) {
        super(uuid);
    }

    @Override
    public boolean onSend(Object[] instructionData) {
        return true;
    }
}
