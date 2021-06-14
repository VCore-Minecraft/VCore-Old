/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.redisson.events;

import de.verdox.vcore.redisson.messages.RedisSimpleMessage;

import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 12.06.2021 23:39
 */
public class RedisMessageEvent {

    private final UUID redisSessionUUID;
    private final RedisSimpleMessage redisSimpleMessage;

    public RedisMessageEvent(UUID redisSessionUUID, RedisSimpleMessage redisSimpleMessage){
        this.redisSessionUUID = redisSessionUUID;
        this.redisSimpleMessage = redisSimpleMessage;
    }

    public RedisSimpleMessage getRedisSimpleMessage() {
        return redisSimpleMessage;
    }

    public UUID getRedisSessionUUID() {
        return redisSessionUUID;
    }

    public boolean isSentByServer(){
        return redisSimpleMessage.getSenderUUID().equals(redisSessionUUID);
    }
}
