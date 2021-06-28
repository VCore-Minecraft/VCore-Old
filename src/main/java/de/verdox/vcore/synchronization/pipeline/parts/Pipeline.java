/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.parts;

import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.parts.cache.GlobalCache;
import de.verdox.vcore.synchronization.pipeline.parts.local.LocalCache;
import de.verdox.vcore.synchronization.pipeline.parts.storage.GlobalStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.06.2021 17:47
 */
public interface Pipeline {

    <T extends VCoreData> T load(@Nonnull Class<? extends T> type, @Nonnull UUID uuid, @Nonnull LoadingStrategy loadingStrategy);

    <T extends VCoreData> T load(@Nonnull Class<? extends T> type, @Nonnull UUID uuid, @Nonnull LoadingStrategy loadingStrategy, boolean createIfNotExist);

    <T extends VCoreData> T load(@Nonnull Class<? extends T> type, @Nonnull UUID uuid, @Nonnull LoadingStrategy loadingStrategy, @Nullable Consumer<T> callback);

    <T extends VCoreData> T load(@Nonnull Class<? extends T> type, @Nonnull UUID uuid, @Nonnull LoadingStrategy loadingStrategy, boolean createIfNotExist, @Nullable Consumer<T> callback);

    <T extends VCoreData> Set<T> loadAllData(@Nonnull Class<? extends T> type, @Nonnull LoadingStrategy loadingStrategy);

    <T extends VCoreData> boolean exist(@Nonnull Class<? extends T> type, @Nonnull UUID uuid, @Nonnull QueryStrategy... strategies);

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