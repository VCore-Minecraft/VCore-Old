package de.verdox.vcore.plugin.subsystem;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.pipeline.datatypes.PlayerData;
import de.verdox.vcore.synchronization.pipeline.datatypes.ServerData;
import de.verdox.vcore.util.global.AnnotationResolver;

import java.util.Set;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 12.02.2022 15:19
 */
public interface VCoreSubsystem<S extends VCorePlugin<?, ?>> {

    boolean isActivated();
    S getVCorePlugin();
    void onSubsystemEnable();
    void onSubsystemDisable();

    /**
     * Provides Set of PlayerDataClasses for the Subsystem
     */
    Set<Class<? extends PlayerData>> playerDataClasses();
    /**
     * Provides Set of ServerDataClasses for the Subsystem
     */
    Set<Class<? extends ServerData>> serverDataClasses();

    default UUID getUuid() {
        return UUID.nameUUIDFromBytes(AnnotationResolver.getDataStorageIdentifier(this.getClass()).getBytes());
    }
}
