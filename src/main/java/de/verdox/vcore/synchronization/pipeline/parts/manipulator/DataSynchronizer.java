/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.parts.manipulator;

import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 01:22
 */
public interface DataSynchronizer {

    /**
     * Cleanup Function triggered when data is removed from cache
     */
    void cleanUp();

    /**
     * Pushes the local data to Pipeline
     */
    void pushUpdate(VCoreData vCoreData, Runnable callback);

    /**
     * Notifies other Servers that hold this data to delete it from local Cache
     */
    void pushRemoval(VCoreData vCoreData, Runnable callback);
}
