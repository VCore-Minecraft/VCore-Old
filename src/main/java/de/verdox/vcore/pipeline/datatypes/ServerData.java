/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.pipeline.datatypes;

import de.verdox.vcore.plugin.VCorePlugin;

import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 01:36
 */
public class ServerData extends VCoreData{
    public ServerData(VCorePlugin<?, ?> plugin, UUID objectUUID) {
        super(plugin, objectUUID);
    }
}
