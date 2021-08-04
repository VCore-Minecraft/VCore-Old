/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.wrapper;

import de.verdox.vcore.plugin.VCorePlugin;

import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 01.08.2021 20:00
 */
public interface PlatformWrapper <T extends VCorePlugin<?,?>> {

    boolean isPlayerOnline(UUID playerUUID);
    boolean isPrimaryThread();

}
