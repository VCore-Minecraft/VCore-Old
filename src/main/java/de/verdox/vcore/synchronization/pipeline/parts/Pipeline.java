/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.parts;

import de.verdox.vcore.plugin.SystemLoadable;
import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.parts.cache.GlobalCache;
import de.verdox.vcore.synchronization.pipeline.parts.local.LocalCache;
import de.verdox.vcore.synchronization.pipeline.parts.storage.GlobalStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.06.2021 17:47
 */
public interface Pipeline extends SystemLoadable {

    @Nullable
    default <T extends VCoreData> T load(@NotNull Class<? extends T> type, @NotNull UUID uuid, @NotNull LoadingStrategy loadingStrategy) {
        return load(type, uuid, loadingStrategy, false, null);
    }

    @NotNull
    default <T extends VCoreData> CompletableFuture<T> loadAsync(@NotNull Class<? extends T> type, @NotNull UUID uuid, @NotNull LoadingStrategy loadingStrategy) {
        return loadAsync(type, uuid, loadingStrategy, false, null);
    }

    @Nullable
    default <T extends VCoreData> T load(@NotNull Class<? extends T> type, @NotNull UUID uuid, @NotNull LoadingStrategy loadingStrategy, boolean createIfNotExist) {
        return load(type, uuid, loadingStrategy, createIfNotExist, null);
    }

    @NotNull
    default <T extends VCoreData> CompletableFuture<T> loadAsync(@NotNull Class<? extends T> type, @NotNull UUID uuid, @NotNull LoadingStrategy loadingStrategy, boolean createIfNotExist) {
        return loadAsync(type, uuid, loadingStrategy, createIfNotExist, null);
    }

    @Nullable
    default <T extends VCoreData> T load(@NotNull Class<? extends T> type, @NotNull UUID uuid, @NotNull LoadingStrategy loadingStrategy, @Nullable Consumer<T> callback) {
        return load(type, uuid, loadingStrategy, false, callback);
    }

    @NotNull
    default <T extends VCoreData> CompletableFuture<T> loadAsync(@NotNull Class<? extends T> type, @NotNull UUID uuid, @NotNull LoadingStrategy loadingStrategy, @Nullable Consumer<T> callback) {
        return loadAsync(type, uuid, loadingStrategy, false, callback);
    }

    @Nullable
    <T extends VCoreData> T load(@NotNull Class<? extends T> type, @NotNull UUID uuid, @NotNull LoadingStrategy loadingStrategy, boolean createIfNotExist, @Nullable Consumer<T> callback);

    @NotNull <T extends VCoreData> CompletableFuture<T> loadAsync(@NotNull Class<? extends T> type, @NotNull UUID uuid, @NotNull LoadingStrategy loadingStrategy, boolean createIfNotExist, @Nullable Consumer<T> callback);

    @NotNull <T extends VCoreData> Set<T> loadAllData(@NotNull Class<? extends T> type, @NotNull LoadingStrategy loadingStrategy);

    @NotNull <T extends VCoreData> CompletableFuture<Set<T>> loadAllDataAsync(@NotNull Class<? extends T> type, @NotNull LoadingStrategy loadingStrategy);

    <T extends VCoreData> boolean exist(@NotNull Class<? extends T> type, @NotNull UUID uuid, @NotNull QueryStrategy... strategies);

    <T extends VCoreData> CompletableFuture<Boolean> existAsync(@NotNull Class<? extends T> type, @NotNull UUID uuid, @NotNull QueryStrategy... strategies);


    /**
     * @param type
     * @param uuid
     * @param notifyOthers When true the Data Manipulator will send a removal message that will remove this instance from all local caches that contain this data
     * @param strategies
     * @param <T>
     * @return
     */
    <T extends VCoreData> boolean delete(@NotNull Class<? extends T> type, @NotNull UUID uuid, boolean notifyOthers, @NotNull QueryStrategy... strategies);

    default <T extends VCoreData> boolean delete(@NotNull Class<? extends T> type, @NotNull UUID uuid, @NotNull QueryStrategy... strategies) {
        return delete(type, uuid, true, strategies);
    }

    default <T extends VCoreData> boolean delete(@NotNull Class<? extends T> type, @NotNull UUID uuid, boolean notifyOthers) {
        return delete(type, uuid, notifyOthers, QueryStrategy.ALL);
    }

    default <T extends VCoreData> boolean delete(@NotNull Class<? extends T> type, @NotNull UUID uuid) {
        return delete(type, uuid, true, QueryStrategy.ALL);
    }

    <T extends VCoreData> CompletableFuture<Boolean> deleteAsync(@NotNull Class<? extends T> type, @NotNull UUID uuid, boolean notifyOthers, @NotNull QueryStrategy... strategies);

    default <T extends VCoreData> CompletableFuture<Boolean> deleteAsync(@NotNull Class<? extends T> type, @NotNull UUID uuid, boolean notifyOthers) {
        return deleteAsync(type, uuid, notifyOthers, QueryStrategy.ALL);
    }

    default <T extends VCoreData> CompletableFuture<Boolean> deleteAsync(@NotNull Class<? extends T> type, @NotNull UUID uuid) {
        return deleteAsync(type, uuid, true, QueryStrategy.ALL);
    }


    LocalCache getLocalCache();

    GlobalCache getGlobalCache();

    GlobalStorage getGlobalStorage();

    void saveAllData();

    void preloadAllData();

    DataSynchronizer getSynchronizer();

    enum LoadingStrategy {
        // Data will be loaded from Local Cache
        LOAD_LOCAL,
        // Data will be loaded from local Cache if not cached it will be loaded into local cache async for the next possible try
        LOAD_LOCAL_ELSE_LOAD,
        // Loads data from PipeLine
        LOAD_PIPELINE
    }

    enum QueryStrategy {
        // Instruction will be executed for Local Cache
        LOCAL,
        // Instruction will be executed for Global Cache
        GLOBAL_CACHE,
        // Instruction will be executed for Global Storage
        GLOBAL_STORAGE,
        // Instruction will be executed for all
        ALL
    }
}