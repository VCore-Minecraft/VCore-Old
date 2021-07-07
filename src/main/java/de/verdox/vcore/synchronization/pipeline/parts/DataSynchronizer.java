/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.parts;

import de.verdox.vcore.plugin.SystemLoadable;
import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.06.2021 15:45
 */
public interface DataSynchronizer extends SystemLoadable {

    CompletableFuture<Boolean> synchronize(@Nonnull DataSourceType source, @Nonnull DataSourceType destination, @Nonnull Class<? extends VCoreData> dataClass, @Nonnull UUID objectUUID);
    CompletableFuture<Boolean> synchronize(@Nonnull DataSourceType source, @Nonnull DataSourceType destination, @Nonnull Class<? extends VCoreData> dataClass, @Nonnull UUID objectUUID, Runnable callback);

    enum DataSourceType {
        LOCAL,
        GLOBAL_CACHE,
        GLOBAL_STORAGE
    }
}
