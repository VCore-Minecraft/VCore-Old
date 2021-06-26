/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.parts;

import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.06.2021 15:39
 */
public interface DataProvider {
    Map<String, Object> loadData(@Nonnull Class<? extends VCoreData> dataClass, @Nonnull UUID objectUUID);
    boolean dataExist(@Nonnull Class<? extends VCoreData> dataClass, @Nonnull UUID objectUUID);
    void save(@Nonnull Class<? extends VCoreData> dataClass, @Nonnull UUID objectUUID, @Nonnull Map<String, Object> dataToSave);
    boolean remove(@Nonnull Class<? extends VCoreData> dataClass, @Nonnull UUID objectUUID);
    Set<UUID> getSavedUUIDs(@Nonnull Class<? extends VCoreData> dataClass);
}
