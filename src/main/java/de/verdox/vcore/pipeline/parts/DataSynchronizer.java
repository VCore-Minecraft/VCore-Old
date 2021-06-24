/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.pipeline.parts;

import de.verdox.vcore.data.datatypes.VCoreData;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.06.2021 15:45
 */
public interface DataSynchronizer {

    void synchronize(@Nonnull DataSourceType source, @Nonnull DataSourceType destination, @Nonnull Class<? extends VCoreData> dataClass, @Nonnull UUID objectUUID);

    enum DataSourceType {
        LOCAL,
        GLOBAL_CACHE,
        GLOBAL_STORAGE
    }
}
