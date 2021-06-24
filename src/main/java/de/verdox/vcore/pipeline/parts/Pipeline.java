/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.pipeline.parts;

import de.verdox.vcore.data.manager.LoadingStrategy;
import de.verdox.vcore.pipeline.datatypes.VCoreData;
import de.verdox.vcore.pipeline.parts.cache.GlobalCache;
import de.verdox.vcore.pipeline.parts.local.LocalCache;
import de.verdox.vcore.pipeline.parts.storage.GlobalStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    LocalCache getLocalCache();
    GlobalCache getGlobalCache();
    GlobalStorage getGlobalStorage();
}
