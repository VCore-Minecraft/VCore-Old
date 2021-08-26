/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.parts.cache.redis;

import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.interfaces.DataManipulator;
import de.verdox.vcore.synchronization.pipeline.parts.DataSynchronizer;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RTopic;
import org.redisson.api.listener.MessageListener;

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
    private final MessageListener<DataBlock> messageListener;
    private final UUID senderUUID = UUID.randomUUID();

    RedisDataManipulator(@NotNull RedisCache redisCache, VCoreData vCoreData) {
        this.redisCache = redisCache;
        this.dataTopic = this.redisCache.getTopic(vCoreData.getClass(), vCoreData.getObjectUUID());
        this.messageListener = (channel, dataBlock) -> {
            if (dataBlock.senderUUID.equals(senderUUID))
                return;
            if (dataBlock instanceof UpdateDataBlock) {
                UpdateDataBlock updateDataBlock = (UpdateDataBlock) dataBlock;
                vCoreData.getPlugin().consoleMessage("&eReceived Sync &b" + vCoreData.getObjectUUID() + " &8[&e" + vCoreData.getClass().getSimpleName() + "&8] &b" + System.currentTimeMillis(), true);
                vCoreData.onSync(vCoreData.deserialize(updateDataBlock.dataToUpdate));
            } else if (dataBlock instanceof RemoveDataBlock) {
                vCoreData.getPlugin().consoleMessage("&eReceived Removal Instruction &b" + vCoreData.getObjectUUID() + " &8[&e" + vCoreData.getClass().getSimpleName() + "&8] &b" + System.currentTimeMillis(), true);
                vCoreData.markForRemoval();
                vCoreData.getPlugin().getServices().getPipeline().delete(vCoreData.getClass(), vCoreData.getObjectUUID(), Pipeline.QueryStrategy.LOCAL);
            }
        };
        dataTopic.addListener(DataBlock.class, messageListener);
    }

    @Override
    public void cleanUp() {
        dataTopic.removeListener(messageListener);
    }

    @Override
    public void pushUpdate(VCoreData vCoreData, Runnable callback) {
        doPush(vCoreData, callback);
    }

    @Override
    public void pushRemoval(VCoreData vCoreData, Runnable callback) {
        vCoreData.markForRemoval();
        dataTopic.publish(new RemoveDataBlock(senderUUID));
        vCoreData.getPlugin().consoleMessage("&ePushing Removal&7: &b" + System.currentTimeMillis(), true);
        if (callback != null)
            callback.run();
    }

    private void doPush(VCoreData vCoreData, Runnable callback) {
        if (vCoreData.isMarkedForRemoval()) {
            vCoreData.getPlugin().consoleMessage("&ePush rejected because markedForRemoval", true);
            return;
        }
        dataTopic.publish(new UpdateDataBlock(senderUUID, vCoreData.serialize()));
        vCoreData.getPlugin().consoleMessage("&ePush Success&7: &b" + System.currentTimeMillis(), true);
        vCoreData.getPlugin().getServices().getPipeline().getSynchronizer().synchronize(DataSynchronizer.DataSourceType.LOCAL, DataSynchronizer.DataSourceType.GLOBAL_CACHE, vCoreData.getClass(), vCoreData.getObjectUUID());
        if (callback != null)
            callback.run();
        callback.run();
    }

    public static class RemoveDataBlock extends DataBlock {
        RemoveDataBlock(@NotNull UUID senderUUID) {
            super(senderUUID);
        }
    }

    public abstract static class DataBlock implements Serializable {
        protected final UUID senderUUID;

        DataBlock(@NotNull UUID senderUUID) {
            this.senderUUID = senderUUID;
        }
    }

    public static class UpdateDataBlock extends DataBlock {
        private final Map<String, Object> dataToUpdate;

        UpdateDataBlock(@NotNull UUID senderUUID, @NotNull Map<String, Object> dataToUpdate) {
            super(senderUUID);
            this.dataToUpdate = dataToUpdate;
        }
    }
}
