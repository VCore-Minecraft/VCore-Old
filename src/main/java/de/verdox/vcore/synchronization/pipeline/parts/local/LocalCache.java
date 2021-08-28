/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.parts.local;

import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.06.2021 15:42
 */
public interface LocalCache {
    @Nullable
    <S extends VCoreData> S getData(@NotNull Class<? extends S> dataClass, @NotNull UUID objectUUID);

    <S extends VCoreData> Set<S> getAllData(@NotNull Class<? extends S> dataClass);

    <S extends VCoreData> void save(@NotNull Class<? extends S> dataClass, @NotNull S data);

    <S extends VCoreData> boolean dataExist(@NotNull Class<? extends S> dataClass, @NotNull UUID objectUUID);

    <S extends VCoreData> boolean remove(@NotNull Class<? extends S> dataClass, @NotNull UUID objectUUID);

    <S extends VCoreData> Set<UUID> getSavedUUIDs(@NotNull Class<? extends S> dataClass);

    <S extends VCoreData> S instantiateData(@NotNull Class<? extends S> dataClass, @NotNull UUID objectUUID);
}
