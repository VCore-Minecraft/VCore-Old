/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging.messages;

import java.io.Serializable;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 15:00
 */
public interface Message extends Serializable {

    UUID getSender();

    String getSenderIdentifier();

    String[] getParameters();

    Object[] dataToSend();

    default int size() {
        if (dataToSend() == null)
            return 0;
        return dataToSend().length;
    }

    default boolean isTypeOf(int index, Class<?> type) {
        if (index < 0 || index >= size())
            return false;
        return dataToSend()[index].getClass().equals(type);
    }

    default boolean isAssignableFrom(int index, Class<?> type) {
        if (index < 0 || index >= size())
            return false;
        return dataToSend()[index].getClass().isAssignableFrom(type);
    }

    default <T> T getData(int index, Class<? extends T> type) {
        if (!isTypeOf(index, type))
            throw new ClassCastException("Cannot cast data in index[" + index + "] to " + type + "!");
        return type.cast(dataToSend()[index]);
    }

}
