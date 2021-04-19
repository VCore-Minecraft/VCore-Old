package de.verdox.vcore.data.events;

import de.verdox.vcore.data.datatypes.VCoreData;

import java.util.UUID;

public abstract class RedisDataEvent {

    private final Class<? extends VCoreData> type;
    private final UUID uuid;

    public RedisDataEvent(Class<? extends VCoreData> type, UUID uuid){
        this.type = type;
        this.uuid = uuid;
    }

    public Class<? extends VCoreData> getType() {
        return type;
    }

    public UUID getUuid() {
        return uuid;
    }
}
