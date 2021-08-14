/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.util.global;

import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import de.verdox.vcore.synchronization.pipeline.annotations.DataStorageIdentifier;
import de.verdox.vcore.synchronization.pipeline.annotations.RequiredSubsystemInfo;
import de.verdox.vcore.synchronization.pipeline.annotations.VCoreDataProperties;
import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;

import javax.annotation.Nonnull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 14.08.2021 18:15
 */
public class AnnotationResolver {
    /**
     * Searches for DataStorageIdentifier and returns the identifier specified in the Annotation
     * This value is used for VCore Storage implementations to identify data.
     * @param customClass Class to search Annotation for
     * @return The Data Identifier of the class
     */

    @Nonnull
    public static String getDataStorageIdentifier(Class<?> customClass){
        DataStorageIdentifier dataStorageIdentifier = customClass.getAnnotation(DataStorageIdentifier.class);
        if(dataStorageIdentifier == null)
            throw new NullPointerException("DataStorageIdentifier not set for class: "+customClass);
        return dataStorageIdentifier.identifier();
    }

    /**
     * Searches for RequiredSubSystemInfo Annotation and returns the class specified in the Annotation
     * @param classType VCoreClassType to check annotation for
     * @throws RuntimeException when no Annotation was found
     * @return The VCoreSubsystem Class specified
     */

    @Nonnull
    public static Class<? extends VCoreSubsystem<?>> findDependSubsystemClass(Class<? extends VCoreData> classType){
        RequiredSubsystemInfo requiredSubsystemInfo = classType.getAnnotation(RequiredSubsystemInfo.class);
        if(requiredSubsystemInfo == null)
            throw new RuntimeException(classType.getName()+" does not have RequiredSubsystemInfo Annotation set");
        return requiredSubsystemInfo.parentSubSystem();
    }

    /**
     * Searches for VCoreDataProperties Annotation of a class and returns it.
     * @param classType The VCoreDataTypeClass to check
     * @throws RuntimeException when no Annotation was found
     * @return The Found VCoreDataProperties Instance if exists
     */

    @Nonnull
    public static VCoreDataProperties getDataProperties(Class<? extends VCoreData> classType){
        VCoreDataProperties vCoreDataProperties = classType.getAnnotation(VCoreDataProperties.class);
        if(vCoreDataProperties == null)
            throw new RuntimeException(classType.getName()+" does not have VCoreDataProperties Annotation set");
        return vCoreDataProperties;
    }
}
