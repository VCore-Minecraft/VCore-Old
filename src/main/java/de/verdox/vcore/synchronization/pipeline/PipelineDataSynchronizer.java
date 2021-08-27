/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline;

import de.verdox.vcore.performance.concurrent.CatchingRunnable;
import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.parts.DataSynchronizer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.06.2021 17:14
 */
public class PipelineDataSynchronizer implements DataSynchronizer {
    private final PipelineManager pipelineManager;

    PipelineDataSynchronizer(PipelineManager pipelineManager) {
        this.pipelineManager = pipelineManager;
    }

    @Override
    public CompletableFuture<Boolean> synchronize(@NotNull DataSourceType source, @NotNull DataSourceType destination, @NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID) {
        return synchronize(source, destination, dataClass, objectUUID, null);
    }

    @Override
    public synchronized CompletableFuture<Boolean> synchronize(@NotNull DataSourceType source, @NotNull DataSourceType destination, @NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID, @Nullable Runnable callback) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        pipelineManager.getExecutorService().submit(new CatchingRunnable(() -> {
            future.complete(doSynchronisation(source, destination, dataClass, objectUUID, callback));
        }));
        return future;
    }

    public boolean doSynchronisation(@NotNull DataSourceType source, @NotNull DataSourceType destination, @NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID, @Nullable Runnable callback) {
        if (source.equals(destination))
            return false;
        if (pipelineManager.globalCache == null && (source.equals(DataSourceType.GLOBAL_CACHE) || destination.equals(DataSourceType.GLOBAL_CACHE)))
            return false;
        if (pipelineManager.globalStorage == null && (source.equals(DataSourceType.GLOBAL_STORAGE) || destination.equals(DataSourceType.GLOBAL_STORAGE)))
            return false;

        //pipelineManager.getPlugin().consoleMessage("&eSyncing &b" + dataClass.getSimpleName() + " &ewith uuid &6" + objectUUID + " &8[&a" + source + " &7-> &b" + destination + "&8] &b" + System.currentTimeMillis(), true);

        if (source.equals(DataSourceType.LOCAL)) {

            if (!pipelineManager.localCache.dataExist(dataClass, objectUUID))
                return false;
            VCoreData data = pipelineManager.localCache.getData(dataClass, objectUUID);
            Map<String, Object> dataToSave = data.serialize();
            dataToSave.remove("_id");
            // Local to Global Cache
            if (destination.equals(DataSourceType.GLOBAL_CACHE))
                pipelineManager.globalCache.save(dataClass, objectUUID, dataToSave);
                // Local to Global Storage
            else if (destination.equals(DataSourceType.GLOBAL_STORAGE))
                pipelineManager.globalStorage.save(dataClass, objectUUID, dataToSave);
        } else if (source.equals(DataSourceType.GLOBAL_CACHE)) {
            if (!pipelineManager.globalCache.dataExist(dataClass, objectUUID))
                return false;
            Map<String, Object> globalCachedData = pipelineManager.globalCache.loadData(dataClass, objectUUID);
            // Error while loading from redis
            if (globalCachedData == null) {
                pipelineManager.getPlugin().consoleMessage("&eTrying to load from storage...", false);
                doSynchronisation(DataSourceType.GLOBAL_STORAGE, DataSourceType.LOCAL, dataClass, objectUUID, callback);
                return false;
            }
            globalCachedData.remove("_id");

            if (destination.equals(DataSourceType.LOCAL)) {
                if (!pipelineManager.localCache.dataExist(dataClass, objectUUID))
                    pipelineManager.localCache.save(dataClass, pipelineManager.localCache.instantiateData(dataClass, objectUUID));
                VCoreData data = pipelineManager.localCache.getData(dataClass, objectUUID);
                data.deserialize(globalCachedData);
                data.loadDependentData();
                data.onLoad();
            } else if (destination.equals(DataSourceType.GLOBAL_STORAGE))
                pipelineManager.globalStorage.save(dataClass, objectUUID, globalCachedData);

        } else if (source.equals(DataSourceType.GLOBAL_STORAGE)) {
            if (!pipelineManager.globalStorage.dataExist(dataClass, objectUUID))
                return false;
            Map<String, Object> globalSavedData = pipelineManager.globalStorage.loadData(dataClass, objectUUID);
            globalSavedData.remove("_id");

            if (destination.equals(DataSourceType.LOCAL)) {
                if (!pipelineManager.localCache.dataExist(dataClass, objectUUID))
                    pipelineManager.localCache.save(dataClass, pipelineManager.localCache.instantiateData(dataClass, objectUUID));
                VCoreData data = pipelineManager.localCache.getData(dataClass, objectUUID);
                data.deserialize(globalSavedData);
                data.loadDependentData();
                data.onLoad();
            } else if (destination.equals(DataSourceType.GLOBAL_CACHE))
                pipelineManager.globalCache.save(dataClass, objectUUID, globalSavedData);
        }
        //pipelineManager.getPlugin().consoleMessage("&eDone syncing &b" + System.currentTimeMillis(), true);
        if (callback != null)
            callback.run();
        return true;
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public void shutdown() {
        try {
            pipelineManager.getPlugin().consoleMessage("&eShutting down Data Synchronizer", false);
            pipelineManager.getExecutorService().shutdown();
            pipelineManager.getExecutorService().awaitTermination(5, TimeUnit.SECONDS);
            pipelineManager.getPlugin().consoleMessage("&aData Synchronizer shut down successfully", false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
