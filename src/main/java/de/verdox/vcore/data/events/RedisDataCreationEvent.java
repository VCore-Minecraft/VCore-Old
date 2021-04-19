package de.verdox.vcore.data.events;

import de.verdox.vcore.data.datatypes.VCoreData;

import java.util.UUID;

public class RedisDataCreationEvent extends RedisDataEvent{
    public RedisDataCreationEvent(Class<? extends VCoreData> type, UUID uuid) {
        super(type, uuid);
    }
}
