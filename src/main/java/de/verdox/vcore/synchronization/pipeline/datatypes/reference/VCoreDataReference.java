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
 * @date 26.08.2021 21:37
 */
public abstract class VCoreDataReference<T extends VCoreData> {
    protected final UUID dataUUID;
    protected final VCorePlugin<?, ?> plugin;
    protected final Class<? extends T> type;

    public VCoreDataReference(@NotNull VCorePlugin<?, ?> plugin, @NotNull Class<? extends T> type, @Nullable UUID dataUUID) {
        this.plugin = plugin;
        this.type = type;
        this.dataUUID = dataUUID;
    }

    @Nullable
    public UUID getDataUUID() {
        return dataUUID;
    }

    @NotNull
    public Class<? extends T> getType() {
        return type;
    }
}
