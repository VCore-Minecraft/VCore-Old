/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.datatypes;

import de.verdox.vcore.plugin.VCorePlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 01.08.2021 19:12
 */
public abstract class NetworkData extends VCoreData {
    public NetworkData(@NotNull VCorePlugin<?, ?> plugin, @NotNull UUID objectUUID) {
        super(plugin, objectUUID);
    }
}
