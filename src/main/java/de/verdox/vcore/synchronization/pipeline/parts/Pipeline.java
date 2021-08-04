/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.parts;

import de.verdox.vcore.plugin.SystemLoadable;
import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.parts.cache.GlobalCache;
import de.verdox.vcore.synchronization.pipeline.parts.local.LocalCache;
import de.verdox.vcore.synchronization.pipeline.parts.storage.GlobalStorage;

import javax.annotation.Nonnull;
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

    default <T extends VCoreData> T load(@Nonnull Class<? extends T> type, @Nonnull UUID uuid, @Nonnull LoadingStrategy loadingStrategy){
        return load(type, uuid, loadingStrategy, false, null);
    }
    default <T extends VCoreData> CompletableFuture<T> loadAsync(@Nonnull Class<? extends T> type, @Nonnull UUID uuid, @Nonnull LoadingStrategy loadingStrategy){
        return loadAsync(type, uuid, loadingStrategy, false, null);
    }

    default <T extends VCoreData> T load(@Nonnull Class<? extends T> type, @Nonnull UUID uuid, @Nonnull LoadingStrategy loadingStrategy, boolean createIfNotExist){
        return load(type, uuid, loadingStrategy, createIfNotExist, null);
    }
    default <T extends VCoreData> CompletableFuture<T> loadAsync(@Nonnull Class<? extends T> type, @Nonnull UUID uuid, @Nonnull LoadingStrategy loadingStrategy, boolean createIfNotExist){
        return loadAsync(type, uuid, loadingStrategy, createIfNotExist, null);
    }

    default <T extends VCoreData> T load(@Nonnull Class<? extends T> type, @Nonnull UUID uuid, @Nonnull LoadingStrategy loadingStrategy, @Nullable Consumer<T> callback){
        return load(type, uuid, loadingStrategy, false, callback);
    }
    default <T extends VCoreData> CompletableFuture<T> loadAsync(@Nonnull Class<? extends T> type, @Nonnull UUID uuid, @Nonnull LoadingStrategy loadingStrategy, @Nullable Consumer<T> callback){
        return loadAsync(type, uuid, loadingStrategy, false, callback);
    }

    <T extends VCoreData> T load(@Nonnull Class<? extends T> type, @Nonnull UUID uuid, @Nonnull LoadingStrategy loadingStrategy, boolean createIfNotExist, @Nullable Consumer<T> callback);
    <T extends VCoreData> CompletableFuture<T> loadAsync(@Nonnull Class<? extends T> type, @Nonnull UUID uuid, @Nonnull LoadingStrategy loadingStrategy, boolean createIfNotExist, @Nullable Consumer<T> callback);

    <T extends VCoreData> Set<T> loadAllData(@Nonnull Class<? extends T> type, @Nonnull LoadingStrategy loadingStrategy);
    <T extends VCoreData> CompletableFuture<Set<T>> loadAllDataAsync(@Nonnull Class<? extends T> type, @Nonnull LoadingStrategy loadingStrategy);

    <T extends VCoreData> boolean exist(@Nonnull Class<? extends T> type, @Nonnull UUID uuid, @Nonnull QueryStrategy... strategies);
    <T extends VCoreData> CompletableFuture<Boolean> existAsync(@Nonnull Class<? extends T> type, @Nonnull UUID uuid, @Nonnull QueryStrategy... strategies);

    //TODO: Bei nem TODO Muss eine Redis Message geschickt werden damit alle das Objekt aus dem lokalen Cache entfernen!
    <T extends VCoreData> boolean delete(@Nonnull Class<? extends T> type, @Nonnull UUID uuid);
    <T extends VCoreData> CompletableFuture<Boolean> deleteAsync(@Nonnull Class<? extends T> type, @Nonnull UUID uuid);

    LocalCache getLocalCache();

    GlobalCache getGlobalCache();

    GlobalStorage getGlobalStorage();

    void saveAllData();
    void preloadAllData();

    DataSynchronizer getSynchronizer();

    enum LoadingStrategy{
        // Data will be loaded from Local Cache
        LOAD_LOCAL,
        // Data will be loaded from local Cache if not cached it will be loaded into local cache async for the next possible try
        LOAD_LOCAL_ELSE_LOAD,
        // Loads data from PipeLine
        LOAD_PIPELINE;
    }

    enum QueryStrategy{
        LOCAL,
        GLOBAL_CACHE,
        GLOBAL_STORAGE,
    }
}