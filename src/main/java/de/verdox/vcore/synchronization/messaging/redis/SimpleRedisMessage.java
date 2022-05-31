/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging.redis;

import de.verdox.vcore.synchronization.messaging.messages.Message;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 22:16
 */
public record SimpleRedisMessage(UUID sender, String senderIdentifier,
                                 String[] parameters, Object[] dataToSend) implements Message {

    public SimpleRedisMessage(@NotNull UUID sender, @NotNull String senderIdentifier, @NotNull String[] parameters, @NotNull Object[] dataToSend) {
        this.sender = sender;
        this.senderIdentifier = senderIdentifier;
        this.parameters = parameters;
        this.dataToSend = dataToSend;
    }

    @Override
    public UUID getSender() {
        return sender;
    }

    @Override
    public String getSenderIdentifier() {
        return senderIdentifier;
    }

    @Override
    public String[] getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "SimpleRedisMessage{" +
                "sender=" + sender +
                ", senderIdentifier='" + senderIdentifier + '\'' +
                ", parameters=" + Arrays.toString(parameters) +
                ", dataToSend=" + Arrays.toString(dataToSend) +
                '}';
    }
}
