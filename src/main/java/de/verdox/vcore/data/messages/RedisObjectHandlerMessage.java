package de.verdox.vcore.data.messages;

import de.verdox.vcore.data.datatypes.VCoreData;

import java.util.UUID;

public class RedisObjectHandlerMessage <S extends VCoreData> implements RedisMessage{

    public final int INSERT = 1;
    public final int DELETE = -1;

    private int type = -2;
    private final Class<? extends S> dataType;
    private UUID uuid;

    public RedisObjectHandlerMessage(Class<? extends S> dataType){
        this.dataType = dataType;
    }

    public RedisObjectHandlerMessage<S> setInsert(){
        this.type = INSERT;
        return this;
    }

    public RedisObjectHandlerMessage<S> setDelete(){
        this.type = DELETE;
        return this;
    }

    public RedisObjectHandlerMessage<S> setUUID(UUID uuid){
        this.uuid = uuid;
        return this;
    }

    public RedisObjectHandlerMessage<S> create(){
        if(this.uuid == null)
            throw new NullPointerException("UUID can't be null!");
        if(type == -2)
            throw new IllegalArgumentException("Type must be set!");
        return this;
    }

    @Override
    public String toString() {
        return "ObjectHandlerMessage{" +
                "INSERT=" + INSERT +
                ", DELETE=" + DELETE +
                ", type=" + type +
                ", dataType=" + dataType +
                ", uuid=" + uuid +
                '}';
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getType() {
        return type;
    }

    public Class<? extends S> getDataType() {
        return dataType;
    }
}
