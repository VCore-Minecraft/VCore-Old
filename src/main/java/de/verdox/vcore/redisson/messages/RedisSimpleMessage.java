/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.redisson.messages;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 12.06.2021 23:32
 */
public class RedisSimpleMessage implements RedisMessage{
    private String pluginName;
    private final UUID senderUUID;
    private String[] parameters;
    private final Object[] dataToSend;

    RedisSimpleMessage(String pluginName, UUID senderUUID, String[] parameters, Object... dataToSend){
        this.pluginName = pluginName;
        this.senderUUID = senderUUID;
        this.parameters = parameters;
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

    public String[] getParameters() {
        return parameters;
    }

    public <S> S getData(Class<? extends S> type, int index){
        return type.cast(getDataToSend()[index]);
    }

    public int size(){
        return getDataToSend().length;
    }

    public static class Builder{

        private final String[] parameters;
        private Object[] dataToSend;

        public Builder(@Nonnull String... parameters){
            this.parameters = parameters;
        }

        public Builder setDataToSend(@Nonnull Object... dataToSend){
            this.dataToSend = dataToSend;
            return this;
        }

        public RedisSimpleMessage constructSimpleMessage(@Nonnull String pluginName, @Nonnull UUID senderUUID){
            if(this.dataToSend == null)
                throw new NullPointerException("You can not send empty messages");
            return new RedisSimpleMessage(pluginName, senderUUID, parameters, dataToSend);
        }
    }
}
