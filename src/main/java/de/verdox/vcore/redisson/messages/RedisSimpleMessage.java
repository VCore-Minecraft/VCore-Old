/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.redisson.messages;

import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 12.06.2021 23:32
 */
public class RedisSimpleMessage implements RedisMessage{
    private String pluginName;
    private final UUID senderUUID;
    private final Object[] dataToSend;

    public RedisSimpleMessage(String pluginName, UUID senderUUID, Object... dataToSend){
        this.pluginName = pluginName;
        this.senderUUID = senderUUID;
        this.dataToSend = dataToSend;
    }

    public Object[] getDataToSend() {
        return dataToSend;
    }

    public UUID getSenderUUID() {
        return senderUUID;
    }

    public boolean isTypeOf(Class<?> type, int index){
        if(index >= size() || index < 0)
            return false;
        return getDataToSend()[index].getClass().equals(type);
    }

    public String getPluginName() {
        return pluginName;
    }

    public <S> S getData(Class<? extends S> type, int index){
        return type.cast(getDataToSend()[index]);
    }

    public int size(){
        return getDataToSend().length;
    }
}
