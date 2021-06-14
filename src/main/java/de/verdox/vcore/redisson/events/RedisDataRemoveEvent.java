package de.verdox.vcore.redisson.events;

import de.verdox.vcore.data.datatypes.VCoreData;

import java.util.UUID;

public class RedisDataRemoveEvent extends RedisDataEvent{
    public RedisDataRemoveEvent(Class<? extends VCoreData> type, UUID uuid) {
        super(type, uuid);
    }
}
