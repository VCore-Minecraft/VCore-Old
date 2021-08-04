/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging.redis;

import de.verdox.vcore.synchronization.messaging.messages.Message;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 22:16
 */
public class SimpleRedisMessage implements Message {

    private final UUID sender;
    private final String senderIdentifier;
    private final String[] parameters;
    private final Object[] dataToSend;

    SimpleRedisMessage(@Nonnull UUID sender, @Nonnull String senderIdentifier, @Nonnull String[] parameters, @Nonnull Object[] dataToSend){
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
    public Object[] dataToSend() {
        return dataToSend;
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
