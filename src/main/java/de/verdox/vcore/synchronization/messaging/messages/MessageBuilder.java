/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging.messages;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 26.06.2021 02:46
 */
public abstract class MessageBuilder {
    protected final UUID sender;
    protected final String senderIdentifier;
    protected String[] parameters;
    protected Object[] dataToSend;

    public MessageBuilder(@NotNull UUID sender, @NotNull String senderIdentifier) {
        Objects.requireNonNull(sender, "sender can't be null!");
        Objects.requireNonNull(senderIdentifier, "senderIdentifier can't be null!");
        this.sender = sender;
        this.senderIdentifier = senderIdentifier;
    }

    public MessageBuilder withParameters(String... parameters) {
        this.parameters = parameters;
        return this;
    }

    public MessageBuilder withData(Object... dataToSend) {
        this.dataToSend = dataToSend;
        return this;
    }

    public abstract Message constructMessage();
}
