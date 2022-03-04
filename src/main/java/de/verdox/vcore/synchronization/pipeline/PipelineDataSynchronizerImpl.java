/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.verdox.vcore.performance.concurrent.CatchingRunnable;
import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.parts.PipelineDataSynchronizer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.06.2021 17:14
 */
public record PipelineDataSynchronizerImpl(
        PipelineImpl pipelineImpl) implements PipelineDataSynchronizer {

    @Override
    public CompletableFuture<Boolean> synchronize(@NotNull DataSourceType source, @NotNull DataSourceType destination, @NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID) {
        return synchronize(source, destination, dataClass, objectUUID, null);
    }

    @Override
    public synchronized CompletableFuture<Boolean> synchronize(@NotNull DataSourceType source, @NotNull DataSourceType destination, @NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID, @Nullable Runnable callback) {
        Objects.requireNonNull(source, "source can't be null!");
        Objects.requireNonNull(destination, "destination can't be null!");
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        pipelineImpl.getExecutorService().submit(new CatchingRunnable(() -> {
            future.complete(doSynchronisation(source, destination, dataClass, objectUUID, callback));
        }));
        return future;
    }

    public synchronized boolean doSynchronisation(@NotNull DataSourceType source, @NotNull DataSourceType destination, @NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID, @Nullable Runnable callback) {
        Objects.requireNonNull(source, "source can't be null!");
        Objects.requireNonNull(destination, "destination can't be null!");
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");
        if (source.equals(destination))
            return false;
        if (pipelineImpl.globalCache == null && (source.equals(DataSourceType.GLOBAL_CACHE) || destination.equals(DataSourceType.GLOBAL_CACHE)))
            return false;
        if (pipelineImpl.globalStorage == null && (source.equals(DataSourceType.GLOBAL_STORAGE) || destination.equals(DataSourceType.GLOBAL_STORAGE)))
            return false;

        //pipelineManager.getPlugin().consoleMessage("&eSyncing &b" + dataClass.getSimpleName() + " &ewith uuid &6" + objectUUID + " &8[&a" + source + " &7-> &b" + destination + "&8] &b" + System.currentTimeMillis(), true);

        if (source.equals(DataSourceType.LOCAL)) {

            if (!pipelineImpl.localCache.dataExist(dataClass, objectUUID))
                return false;
            VCoreData data = pipelineImpl.localCache.getData(dataClass, objectUUID);
            if (data == null)
                return false;
            JsonElement dataToSave = data.serialize();
            // Local to Global Cache
            if (destination.equals(DataSourceType.GLOBAL_CACHE))
                pipelineImpl.globalCache.save(dataClass, objectUUID, dataToSave);
                // Local to Global Storage
            else if (destination.equals(DataSourceType.GLOBAL_STORAGE))
                pipelineImpl.globalStorage.save(dataClass, objectUUID, dataToSave);
        } else if (source.equals(DataSourceType.GLOBAL_CACHE)) {
            if (!pipelineImpl.globalCache.dataExist(dataClass, objectUUID))
                return false;
            JsonObject globalCachedData = pipelineImpl.globalCache.loadData(dataClass, objectUUID).getAsJsonObject();
            // Error while loading from redis
            if (globalCachedData == null) {
                pipelineImpl.getPlugin().consoleMessage("&eTrying to load from storage...", false);
                doSynchronisation(DataSourceType.GLOBAL_STORAGE, DataSourceType.LOCAL, dataClass, objectUUID, callback);
                return false;
            }

            if (destination.equals(DataSourceType.LOCAL)) {
                if (!pipelineImpl.localCache.dataExist(dataClass, objectUUID))
                    pipelineImpl.localCache.save(dataClass, pipelineImpl.localCache.instantiateData(dataClass, objectUUID));
                VCoreData data = pipelineImpl.localCache.getData(dataClass, objectUUID);
                if (data == null)
                    return false;
                data.deserialize(globalCachedData);
                data.loadDependentData();
                data.onLoad();
            } else if (destination.equals(DataSourceType.GLOBAL_STORAGE))
                pipelineImpl.globalStorage.save(dataClass, objectUUID, globalCachedData);

        } else if (source.equals(DataSourceType.GLOBAL_STORAGE)) {
            if (!pipelineImpl.globalStorage.dataExist(dataClass, objectUUID))
                return false;
            JsonObject globalSavedData = pipelineImpl.globalStorage.loadData(dataClass, objectUUID).getAsJsonObject();

            if (destination.equals(DataSourceType.LOCAL)) {
                if (!pipelineImpl.localCache.dataExist(dataClass, objectUUID))
                    pipelineImpl.localCache.save(dataClass, pipelineImpl.localCache.instantiateData(dataClass, objectUUID));
                VCoreData data = pipelineImpl.localCache.getData(dataClass, objectUUID);
                if (data == null)
                    return false;
                data.deserialize(globalSavedData);
                data.loadDependentData();
                data.onLoad();
            } else if (destination.equals(DataSourceType.GLOBAL_CACHE))
                pipelineImpl.globalCache.save(dataClass, objectUUID, globalSavedData);
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
            pipelineImpl.getPlugin().consoleMessage("&eShutting down Data Synchronizer", false);
            pipelineImpl.getExecutorService().shutdown();
            pipelineImpl.getExecutorService().awaitTermination(5, TimeUnit.SECONDS);
            pipelineImpl.getPlugin().consoleMessage("&aData Synchronizer shut down successfully", false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
