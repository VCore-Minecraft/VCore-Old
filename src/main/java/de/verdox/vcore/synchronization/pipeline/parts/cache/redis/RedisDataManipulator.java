/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.parts.cache.redis;

import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.interfaces.DataManipulator;
import de.verdox.vcore.synchronization.pipeline.parts.DataSynchronizer;
import org.redisson.api.RTopic;
import org.redisson.api.listener.MessageListener;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 01:24
 */
public class RedisDataManipulator implements DataManipulator {

    private final RedisCache redisCache;
    private final RTopic dataTopic;
    private final MessageListener<UpdateDataBlock> messageListener;
    private final UUID senderUUID = UUID.randomUUID();

    RedisDataManipulator(@Nonnull RedisCache redisCache, VCoreData vCoreData){
        this.redisCache = redisCache;
        this.dataTopic = this.redisCache.getTopic(vCoreData.getClass(), vCoreData.getObjectUUID());
        this.messageListener = (channel, updateDataBlock) -> {
            if(updateDataBlock.senderUUID.equals(senderUUID))
                return;
            vCoreData.getPlugin().consoleMessage("&eReceived Redis Sync &b"+vCoreData.getObjectUUID()+" &8[&e"+vCoreData.getClass().getSimpleName()+"&8] &b"+System.currentTimeMillis(),true);
            vCoreData.onSync(vCoreData.deserialize(updateDataBlock.dataToUpdate));
            vCoreData.getPlugin().consoleMessage("&eRedis Sync complete &b"+System.currentTimeMillis(),true);
        };
        dataTopic.addListener(UpdateDataBlock.class,messageListener);
    }

    @Override
    public void cleanUp() {
        dataTopic.removeListener(messageListener);
    }

    @Override
    public void pushUpdate(VCoreData vCoreData, Runnable callback) {
        doPush(vCoreData, callback);
    }

    private void doPush(VCoreData vCoreData, Runnable callback){
        dataTopic.publish(new UpdateDataBlock(senderUUID,vCoreData.serialize()));
        vCoreData.getPlugin().consoleMessage("&ePush Success&7: &b"+System.currentTimeMillis(),true);
        vCoreData.getPlugin().getServices().getPipeline().getSynchronizer().synchronize(DataSynchronizer.DataSourceType.LOCAL, DataSynchronizer.DataSourceType.GLOBAL_CACHE, vCoreData.getClass(), vCoreData.getObjectUUID());
        callback.run();
    }

    public static class UpdateDataBlock implements Serializable {
        private final UUID senderUUID;
        private final Map<String, Object> dataToUpdate;

        UpdateDataBlock(@Nonnull UUID senderUUID, @Nonnull Map<String, Object> dataToUpdate){
            this.senderUUID = senderUUID;
            this.dataToUpdate = dataToUpdate;
        }
    }
}
