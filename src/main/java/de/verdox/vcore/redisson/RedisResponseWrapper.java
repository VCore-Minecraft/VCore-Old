/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.redisson;

import de.verdox.vcore.redisson.events.RedisMessageEvent;
import de.verdox.vcore.redisson.messages.RedisSimpleMessage;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 15.06.2021 00:14
 */
public class RedisResponseWrapper {
    private final RedisMessageEvent redisMessageEvent;
    private final Class<?>[] types;

    public RedisResponseWrapper(RedisMessageEvent redisMessageEvent, Class<?>... types){
        this.redisMessageEvent = redisMessageEvent;
        this.types = types;
    }

    public boolean validate(){
        if(redisMessageEvent.getRedisSimpleMessage().size() != types.length)
            return false;
        RedisSimpleMessage redisSimpleMessage = redisMessageEvent.getRedisSimpleMessage();

        for (int i = 0; i < types.length; i++) {
            Class<?> type = types[i];
            if(!redisSimpleMessage.isTypeOf(type, i))
                return false;
        }
        return true;
    }

    public boolean parameterContains(String... parameters){

        for (int i = 0; i < redisMessageEvent.getRedisSimpleMessage().getParameters().length; i++) {
            String messageParameter = redisMessageEvent.getRedisSimpleMessage().getParameters()[i];
            if(i >= parameters.length)
                continue;
            String neededParameter = parameters[i];
            if(!messageParameter.equals(neededParameter))
                return false;
        }
        return true;
    }

    public RedisSimpleMessage getRedisSimpleMessage() {
        return redisMessageEvent.getRedisSimpleMessage();
    }
}
