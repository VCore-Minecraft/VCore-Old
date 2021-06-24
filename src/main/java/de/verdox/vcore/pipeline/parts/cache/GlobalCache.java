/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.pipeline.parts.cache;

import de.verdox.vcore.pipeline.annotations.DataContext;
import de.verdox.vcore.pipeline.annotations.PreloadStrategy;
import de.verdox.vcore.pipeline.annotations.VCoreDataContext;
import de.verdox.vcore.pipeline.datatypes.VCoreData;
import de.verdox.vcore.pipeline.interfaces.DataManipulator;
import de.verdox.vcore.pipeline.parts.DataProvider;

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

    boolean dataExist(@Nonnull Class<? extends VCoreData> dataClass, @Nonnull UUID objectUUID);

    default DataManipulator constructDataManipulator(VCoreData vCoreData){
        return new DataManipulator() {
            @Override
            public void cleanUp() {

            }

            @Override
            public void pushUpdate(VCoreData vCoreData, boolean async) {

            }
        };
    }

    static DataContext getContext(Class<? extends VCoreData> dataClass){
        VCoreDataContext vCoreDataContext = dataClass.getAnnotation(VCoreDataContext.class);
        if(vCoreDataContext == null)
            return DataContext.GLOBAL;
        return vCoreDataContext.dataContext();
    }

    static PreloadStrategy getPreloadStrategy(Class<? extends VCoreData> dataClass){
        VCoreDataContext vCoreDataContext = dataClass.getAnnotation(VCoreDataContext.class);
        if(vCoreDataContext == null)
            return PreloadStrategy.LOAD_ON_NEED;
        return vCoreDataContext.preloadStrategy();
    }
}
