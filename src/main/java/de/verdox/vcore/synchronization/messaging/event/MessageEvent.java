/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging.event;

import de.verdox.vcore.synchronization.messaging.messages.Message;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 22:10
 */
public class MessageEvent {
    private final String channelName;
    private final Message message;

    public MessageEvent(@NotNull String channelName, @NotNull Message message) {
        Objects.requireNonNull(channelName, "channelName can't be null!");
        Objects.requireNonNull(message, "message can't be null!");
        this.channelName = channelName;
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public String getChannelName() {
        return channelName;
    }
}
