/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.parts.storage;

import de.verdox.vcore.synchronization.pipeline.annotations.DataStorageIdentifier;
import de.verdox.vcore.synchronization.pipeline.parts.DataProvider;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.06.2021 15:25
 */
public interface GlobalStorage extends DataProvider {
    static String getDataStorageIdentifier(Class<?> customClass){
        DataStorageIdentifier dataStorageIdentifier = customClass.getAnnotation(DataStorageIdentifier.class);
        if(dataStorageIdentifier == null)
            throw new NullPointerException("DataStorageIdentifier not set for class: "+customClass);
        return dataStorageIdentifier.identifier();
    }
}
