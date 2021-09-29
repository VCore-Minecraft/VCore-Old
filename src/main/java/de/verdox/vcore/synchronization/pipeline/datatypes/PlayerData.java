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
 * @date 25.06.2021 01:36
 */
public abstract class PlayerData extends VCoreData {
    public PlayerData(@NotNull VCorePlugin<?, ?> plugin, @NotNull UUID objectUUID) {
        super(plugin, objectUUID);
    }

    /**
     * Is called async when player disconnects
     *
     * @param playerUUID PlayerUUID
     */
    public void onDisconnect(UUID playerUUID) {
    }

    /**
     * Is called async when player disconnects
     *
     * @param playerUUID PlayerUUID
     */
    public void onConnect(UUID playerUUID) {
    }
}
