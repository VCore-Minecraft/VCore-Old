/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.modules;

import de.verdox.vcore.plugin.VCorePlugin;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 31.01.2022 19:23
 */
public interface VCoreModule<T extends VCorePlugin<?, ?>> {
    void enableModule(T plugin);

    void disableModule();
}
