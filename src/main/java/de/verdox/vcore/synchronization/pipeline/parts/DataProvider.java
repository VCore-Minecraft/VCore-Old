/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.parts;

import com.google.gson.JsonElement;
import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.06.2021 15:39
 */
public interface DataProvider {
    JsonElement loadData(@NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID);

    boolean dataExist(@NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID);

    void save(@NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID, @NotNull JsonElement dataToSave);

    boolean remove(@NotNull Class<? extends VCoreData> dataClass, @NotNull UUID objectUUID);

    Set<UUID> getSavedUUIDs(@NotNull Class<? extends VCoreData> dataClass);
}
