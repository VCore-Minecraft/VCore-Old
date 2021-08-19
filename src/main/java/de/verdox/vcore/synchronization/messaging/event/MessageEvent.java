/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging.event;

import de.verdox.vcore.synchronization.messaging.messages.Message;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 22:10
 */
public class MessageEvent {
    private final Message message;

    public MessageEvent(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
