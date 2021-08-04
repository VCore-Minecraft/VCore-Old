/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging;


import de.verdox.vcore.plugin.SystemLoadable;
import de.verdox.vcore.synchronization.messaging.messages.Message;
import de.verdox.vcore.synchronization.messaging.messages.MessageBuilder;
import de.verdox.vcore.synchronization.messaging.query.QueryService;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 14:54
 */
public interface MessagingService<T extends MessageBuilder> extends SystemLoadable {

    T constructMessage();
    void publishMessage(Message message);

    boolean isOwnMessage(Message message);
    default UUID getSessionUUID(){
        return UUID.randomUUID();
    }
    String getSenderName();

    QueryService getQueryService();
}
