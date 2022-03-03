/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.parts.storage;

import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import de.verdox.vcore.synchronization.pipeline.datatypes.NetworkData;
import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.parts.DataProvider;
import de.verdox.vcore.util.global.AnnotationResolver;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.06.2021 15:25
 */
public interface GlobalStorage extends DataProvider {

    default String getSuffix(@NotNull Class<? extends VCoreData> dataClass) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        return AnnotationResolver.getDataStorageIdentifier(dataClass);
    }

    default String getStoragePath(@NotNull Class<? extends VCoreData> dataClass, @NotNull String suffix, @NotNull String separator) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        Objects.requireNonNull(suffix, "suffix can't be null!");
        if (NetworkData.class.isAssignableFrom(dataClass)) {
            return "VCore_NetworkData" + separator + dataClass.getCanonicalName() + separator + suffix;
        } else {
            Class<? extends VCoreSubsystem<?>> subsystemClass = AnnotationResolver.findDependSubsystemClass(dataClass);
            return AnnotationResolver.getDataStorageIdentifier(subsystemClass) + separator + suffix;
        }
    }
}
