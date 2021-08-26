/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.parts.cache;

import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.interfaces.DataManipulator;
import de.verdox.vcore.synchronization.pipeline.parts.DataProvider;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.06.2021 15:25
 */
public interface GlobalCache extends DataProvider {

    Map<String, Object> getObjectCache(Class<? extends VCoreData> dataClass, UUID objectUUID);

    Set<Map<String, Object>> getCacheList(Class<? extends VCoreData> dataClass);

    Set<String> getKeys(Class<? extends VCoreData> dataClass);

    Map<String, Object> getGlobalCacheMap(String name);

    boolean dataExist(@Nonnull Class<? extends VCoreData> dataClass, @Nonnull UUID objectUUID);

    default DataManipulator constructDataManipulator(VCoreData vCoreData) {
        return new DataManipulator() {
            @Override
            public void cleanUp() {

            }

            @Override
            public void pushUpdate(VCoreData vCoreData, Runnable callback) {

            }

            @Override
            public void pushRemoval(VCoreData vCoreData, Runnable callback) {


            }
        };
    }

}
