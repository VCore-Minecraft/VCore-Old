/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.parts.manipulator.redis;

import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.parts.PipelineDataSynchronizer;
import de.verdox.vcore.synchronization.pipeline.parts.cache.redis.RedisCache;
import de.verdox.vcore.synchronization.pipeline.parts.local.LocalCache;
import de.verdox.vcore.synchronization.pipeline.parts.manipulator.DataSynchronizer;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RTopic;
import org.redisson.api.listener.MessageListener;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 01:24
 */
public class RedisDataSynchronizer implements DataSynchronizer {

    private final RedisCache redisCache;
    private final RTopic dataTopic;
    private final MessageListener<DataBlock> messageListener;
    private final UUID senderUUID = UUID.randomUUID();

    RedisDataSynchronizer(@NotNull LocalCache localCache, @NotNull RedisCache redisCache, @NotNull Class<? extends VCoreData> dataClass) {
        Objects.requireNonNull(redisCache, "redisCache can't be null!");
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        this.redisCache = redisCache;
        this.dataTopic = this.redisCache.getTopic(dataClass);
        this.messageListener = (channel, dataBlock) -> {
            if (dataBlock.senderUUID.equals(senderUUID))
                return;
            VCoreData vCoreData = localCache.getData(dataClass,dataBlock.dataUUID);
            if(vCoreData == null)
                return;
            if (dataBlock instanceof UpdateDataBlock updateDataBlock) {
                vCoreData.getPlugin().consoleMessage("&eReceived Sync &b" + vCoreData.getObjectUUID() + " &8[&e" + vCoreData.getClass().getSimpleName() + "&8] &b" + System.currentTimeMillis(), true);
                vCoreData.onSync(vCoreData.deserialize(updateDataBlock.dataToUpdate));
            } else if (dataBlock instanceof RemoveDataBlock) {
                vCoreData.getPlugin().consoleMessage("&eReceived Removal Instruction &b" + vCoreData.getObjectUUID() + " &8[&e" + vCoreData.getClass().getSimpleName() + "&8] &b" + System.currentTimeMillis(), true);
                vCoreData.markForRemoval();
                vCoreData.getPlugin().getServices().getPipeline().delete(vCoreData.getClass(), vCoreData.getObjectUUID(), false, Pipeline.QueryStrategy.LOCAL);
            }
        };
        dataTopic.addListener(DataBlock.class, messageListener);
    }

    @Override
    public void cleanUp() {
        dataTopic.removeListener(messageListener);
    }

    @Override
    public void pushUpdate(@NotNull VCoreData vCoreData, @Nullable Runnable callback) {
        doPush(vCoreData, callback);
    }

    @Override
    public void pushRemoval(@NotNull VCoreData vCoreData, @Nullable Runnable callback) {
        Objects.requireNonNull(vCoreData, "vCoreData can't be null!");
        vCoreData.markForRemoval();
        dataTopic.publish(new RemoveDataBlock(senderUUID,vCoreData.getObjectUUID()));
        vCoreData.getPlugin().consoleMessage("&ePushing Removal&7: &b" + System.currentTimeMillis(), true);
        if (callback != null)
            callback.run();
    }

    private void doPush(@NotNull VCoreData vCoreData, @Nullable Runnable callback) {
        Objects.requireNonNull(vCoreData, "vCoreData can't be null!");
        if (vCoreData.isMarkedForRemoval()) {
            vCoreData.getPlugin().consoleMessage("&4Push rejected as it is marked for removal &b" + vCoreData.getObjectUUID() + " &8[&e" + vCoreData.getClass().getSimpleName() + "&8] &b" + System.currentTimeMillis(), true);
            return;
        }
        dataTopic.publish(new UpdateDataBlock(senderUUID, vCoreData.getObjectUUID(), vCoreData.serialize()));
        vCoreData.getPlugin().consoleMessage("&ePushing Sync &b" + vCoreData.getObjectUUID() + " &8[&e" + vCoreData.getClass().getSimpleName() + "&8] &b" + System.currentTimeMillis(), true);
        vCoreData.getPlugin().getServices().getPipeline().getPipelineDataSynchronizer().synchronize(PipelineDataSynchronizer.DataSourceType.LOCAL, PipelineDataSynchronizer.DataSourceType.GLOBAL_CACHE, vCoreData.getClass(), vCoreData.getObjectUUID());
        if (callback != null)
            callback.run();
    }

    public abstract static class DataBlock implements Serializable {
        protected final UUID senderUUID;
        protected final UUID dataUUID;

        DataBlock(@NotNull UUID senderUUID, @NotNull UUID dataUUID) {
            this.senderUUID = senderUUID;
            this.dataUUID = dataUUID;
        }
    }

    public static class RemoveDataBlock extends DataBlock {
        RemoveDataBlock(@NotNull UUID senderUUID, @NotNull UUID dataUUID) {
            super(senderUUID, dataUUID);
        }
    }

    public static class UpdateDataBlock extends DataBlock {
        private final Map<String, Object> dataToUpdate;

        UpdateDataBlock(@NotNull UUID senderUUID, @NotNull UUID dataUUID, Map<String, Object> dataToUpdate) {
            super(senderUUID, dataUUID);
            this.dataToUpdate = dataToUpdate;
        }
    }
}
