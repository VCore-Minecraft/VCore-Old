/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging;


import de.verdox.vcore.synchronization.messaging.messages.Message;

import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 14:54
 */
public interface MessagingService {

    Message constructMessage(UUID sender, String senderIdentifier, String[] parameters, Object... dataToSend);
    void publishMessage(Message message);

    default UUID getSessionUUID(){
        return UUID.randomUUID();
    }
    String getSenderName();

}
