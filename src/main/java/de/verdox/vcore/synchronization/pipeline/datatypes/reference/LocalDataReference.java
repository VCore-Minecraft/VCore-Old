/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.datatypes.reference;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 26.08.2021 21:42
 */
public class LocalDataReference<T extends VCoreData> extends VCoreDataReference<T> {
    public LocalDataReference(@NotNull VCorePlugin<?, ?> plugin, @NotNull Class<? extends T> type, @Nullable UUID dataUUID) {
        super(plugin, type, dataUUID);
    }

    @Nullable
    public T get() {
        if (dataUUID == null)
            return null;
        if (!plugin.getServices().getPipeline().getLocalCache().dataExist(type, dataUUID))
            return null;
        T data = plugin.getServices().getPipeline().getLocalCache().getData(type, dataUUID);
        if (data != null && data.isMarkedForRemoval())
            return null;
        return data;
    }
}
