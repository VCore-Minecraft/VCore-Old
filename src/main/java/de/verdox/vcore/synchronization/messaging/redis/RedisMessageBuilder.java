/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging.redis;

import de.verdox.vcore.synchronization.messaging.messages.Message;
import de.verdox.vcore.synchronization.messaging.messages.MessageBuilder;

import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 26.06.2021 02:48
 */
public class RedisMessageBuilder extends MessageBuilder {
    public RedisMessageBuilder(UUID sender, String senderIdentifier) {
        super(sender, senderIdentifier);
    }

    @Override
    public Message constructMessage() {
        return new SimpleRedisMessage(sender, senderIdentifier, parameters, dataToSend);
    }
}
