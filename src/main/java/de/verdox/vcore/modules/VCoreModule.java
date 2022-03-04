/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.modules;

import de.verdox.vcore.plugin.VCoreCoreInstance;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 31.01.2022 19:23
 */

/**
 * @param <T> Platform dependent Plugin (e.g. JavaPlugin...)
 * @param <R> Platform dependent Subsystem Implementation
 * @param <C> Platform dependent VCoreInstance Implementation
 */
public interface VCoreModule <T, R extends VCoreSubsystem<?>, C extends VCoreCoreInstance<T,R>> extends VCorePlugin<T,R> {
    void enableModule(C coreInstance);
    void disableModule();
}
