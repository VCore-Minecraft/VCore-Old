/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.pipeline;

import de.verdox.vcore.data.datatypes.VCoreData;
import de.verdox.vcore.pipeline.parts.DataSynchronizer;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.06.2021 17:14
 */
public class PipelineDataSynchronizer implements DataSynchronizer {
    private final PipelineManager pipelineManager;

    PipelineDataSynchronizer(PipelineManager pipelineManager){
        this.pipelineManager = pipelineManager;
    }

    @Override
    public void synchronize(@Nonnull DataSourceType source, @Nonnull DataSourceType destination, @Nonnull Class<? extends VCoreData> dataClass, @Nonnull UUID objectUUID) {
        if(source.equals(destination))
            return;

        if(source.equals(DataSourceType.LOCAL)){
            if(!pipelineManager.localCache.exist(dataClass, objectUUID))
                return;
            VCoreData data = pipelineManager.localCache.getData(dataClass, objectUUID);
            Map<String, Object> dataToSave = data.dataForRedis();
            // Local to Global Cache
            if(destination.equals(DataSourceType.GLOBAL_CACHE))
                pipelineManager.globalCache.save(dataClass,objectUUID,dataToSave);
            // Local to Global Storage
            else if(destination.equals(DataSourceType.GLOBAL_STORAGE))
                pipelineManager.globalStorage.save(dataClass, objectUUID, dataToSave);
        }
        else if(source.equals(DataSourceType.GLOBAL_CACHE)){
            if(!pipelineManager.globalCache.dataExist(dataClass, objectUUID))
                return;
            Map<String, Object> globalCachedData = pipelineManager.globalCache.loadData(dataClass, objectUUID);

            if(destination.equals(DataSourceType.LOCAL)) {
                if (!pipelineManager.localCache.exist(dataClass, objectUUID))
                    pipelineManager.localCache.save(dataClass, pipelineManager.localCache.instantiateData(dataClass, objectUUID));
                pipelineManager.localCache.getData(dataClass, objectUUID).restoreFromRedis(globalCachedData);
            }
            else if(destination.equals(DataSourceType.GLOBAL_STORAGE))
                pipelineManager.globalStorage.save(dataClass, objectUUID, globalCachedData);
        }
        else if(source.equals(DataSourceType.GLOBAL_STORAGE)){
            if(!pipelineManager.globalStorage.dataExist(dataClass, objectUUID))
                return;
            Map<String, Object> globalSavedData = pipelineManager.globalStorage.loadData(dataClass, objectUUID);

            if(destination.equals(DataSourceType.LOCAL)) {
                if (!pipelineManager.localCache.exist(dataClass, objectUUID))
                    pipelineManager.localCache.save(dataClass, pipelineManager.localCache.instantiateData(dataClass, objectUUID));
                pipelineManager.localCache.getData(dataClass, objectUUID).restoreFromDataBase(globalSavedData);
            }
            else if(destination.equals(DataSourceType.GLOBAL_CACHE))
                pipelineManager.globalCache.save(dataClass,objectUUID,globalSavedData);
        }
    }
}
