/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging.messages;

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

    public MessageBuilder(UUID sender, String senderIdentifier){
        this.sender = sender;
        this.senderIdentifier = senderIdentifier;
    }

    public MessageBuilder withParameters(String... parameters){
        this.parameters = parameters;
        return this;
    }

    public MessageBuilder withData(Object... dataToSend){
        this.dataToSend = dataToSend;
        return this;
    }

    public abstract Message constructMessage();
}
