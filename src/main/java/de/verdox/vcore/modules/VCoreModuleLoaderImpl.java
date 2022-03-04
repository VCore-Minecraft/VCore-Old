package de.verdox.vcore.modules;

import de.verdox.vcore.plugin.VCoreCoreInstance;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.02.2022 23:25
 */

/**
 * @param <T> Platform dependent Plugin (e.g. JavaPlugin...)
 * @param <R> Platform dependent Subsystem Implementation
 * @param <C> Platform dependent VCoreInstance Implementation
 * @param <V> Platform dependent VCoreModule Implementation
 */
public class VCoreModuleLoaderImpl<T, R extends VCoreSubsystem<?>, C extends VCoreCoreInstance<T, R>, V extends VCoreModule<T, R, C>> implements VCoreModuleLoader<T, R, C, V> {

    private final C coreInstance;
    private final Map<Class<? extends V>, V> cache = new ConcurrentHashMap<>();

    public VCoreModuleLoaderImpl(C coreInstance) {
        this.coreInstance = coreInstance;
    }

    @Override
    public void registerModule(V module) {
        if (cache.containsKey(module.getClass()))
            throw new RuntimeException("Module already registered!");
        cache.put((Class<? extends V>) module.getClass(), module);
        module.enableModule(coreInstance);
        coreInstance.consoleMessage("&eModule loaded&7: &a" + module.getClass().getSimpleName(), true);
    }

    @Override
    public <S extends V> S getModule(Class<S> type) {
        if (!cache.containsKey(type))
            throw new NullPointerException("Module not registered yet!");
        return type.cast(cache.get(type));
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public void shutdown() {
        cache.values().forEach(VCoreModule::disableModule);
    }
}
