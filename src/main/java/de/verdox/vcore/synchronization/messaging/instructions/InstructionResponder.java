/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging.instructions;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.08.2021 21:36
 */
public interface InstructionResponder {
    Object[] respondToInstruction(Object[] instructionData);

    boolean respondToItself();
}
