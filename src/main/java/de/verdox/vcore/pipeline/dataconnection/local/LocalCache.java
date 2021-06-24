/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.pipeline.dataconnection.local;

import de.verdox.vcore.data.datatypes.VCoreData;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.06.2021 15:42
 */
public interface LocalCache {
    <S extends VCoreData> S getData(@Nonnull Class<? extends S> dataClass, @Nonnull UUID objectUUID);
    <S extends VCoreData> void save(@Nonnull Class<? extends S> dataClass, @Nonnull S data);
    <S extends VCoreData> boolean exist(@Nonnull Class<? extends S> dataClass, @Nonnull UUID objectUUID);
    <S extends VCoreData> boolean delete(@Nonnull Class<? extends S> dataClass, @Nonnull UUID objectUUID);
    <S extends VCoreData> Set<UUID> getSavedUUIDs(@Nonnull Class<? extends S> dataClass);
    <S extends VCoreData> S instantiateData(@Nonnull Class<? extends S> dataClass, @Nonnull UUID objectUUID);
}
