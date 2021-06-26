/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging.messages;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 22:37
 */
public class MessageWrapper {
    private final Message message;

    public MessageWrapper(Message message){
        this.message = message;
    }

    public boolean validate(Class<?>... types){
        if(message.size() != types.length)
            return false;

        for (int i = 0; i < types.length; i++) {
            Class<?> type = types[i];
            if(!message.isTypeOf(i, type))
                return false;
        }
        return true;
    }

    public boolean parameterContains(String... parameters){
        if(message.getParameters() == null)
            return false;
        for (int i = 0; i < message.getParameters().length; i++) {
            String messageParameter = message.getParameters()[i];
            if(i >= parameters.length)
                continue;
            String neededParameter = parameters[i];
            if(!messageParameter.equals(neededParameter))
                return false;
        }
        return true;
    }

}
