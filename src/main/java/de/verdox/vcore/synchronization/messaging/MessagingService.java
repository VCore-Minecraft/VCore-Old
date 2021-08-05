/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging;


import de.verdox.vcore.plugin.SystemLoadable;
import de.verdox.vcore.synchronization.messaging.instructions.InstructionService;
import de.verdox.vcore.synchronization.messaging.messages.Message;
import de.verdox.vcore.synchronization.messaging.messages.MessageBuilder;

import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 14:54
 */
public interface MessagingService<T extends MessageBuilder> extends SystemLoadable {

    //TODO: Jeder Messaging Channel bekommt für diese eine Session einen direkten Nachrichtenkanal, an welchen er genauso Nachrichten empfangen kann
    // Das Message Event soll auch da gecalled werden
    // Beim InstructionService wo querys und updates versendet werden soll die Möglichkeit gegeben werden ServerNamen (String... serverNames) anzugeben, als
    // direkte Ziele für die Instruktion

    T constructMessage();
    void publishMessage(Message message);

    boolean isOwnMessage(Message message);
    default UUID getSessionUUID(){
        return UUID.randomUUID();
    }
    String getSenderName();

    InstructionService getInstructionService();
}
