package de.verdox.vcore.modules;

import de.verdox.vcore.plugin.SystemLoadable;
import de.verdox.vcore.plugin.VCoreCoreInstance;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.02.2022 23:18
 */

/**
 *
 * @param <T> Platform dependent Plugin (e.g. JavaPlugin...)
 * @param <R> Platform dependent Subsystem Implementation
 * @param <C> Platform dependent CoreInstance Implementation
 * @param <V> Platform dependent Module Implementation
 */
public interface VCoreModuleLoader <T,R extends VCoreSubsystem<?>, C extends VCoreCoreInstance<T,R>, V extends VCoreModule<T,R,C>> extends SystemLoadable {
    void registerModule(V module);
    <S extends V> S getModule(Class<S> type);
}