/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.datatypes;

import de.verdox.vcore.plugin.VCorePlugin;

import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 01:36
 */
public abstract class PlayerData extends VCoreData {
    public PlayerData(VCorePlugin<?, ?> plugin, UUID objectUUID) {
        super(plugin, objectUUID);
    }

    public void onDisconnect(UUID playerUUID) {
    }

    public void onConnect(UUID playerUUID) {
    }
}
