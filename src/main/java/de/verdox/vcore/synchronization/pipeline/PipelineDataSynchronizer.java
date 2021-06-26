/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline;

import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.parts.DataSynchronizer;

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
    public synchronized void synchronize(@Nonnull DataSourceType source, @Nonnull DataSourceType destination, @Nonnull Class<? extends VCoreData> dataClass, @Nonnull UUID objectUUID) {
        if(source.equals(destination))
            return;

        if(pipelineManager.globalCache == null && (source.equals(DataSourceType.GLOBAL_CACHE) || destination.equals(DataSourceType.GLOBAL_CACHE)))
            return;
        if(pipelineManager.globalStorage == null && (source.equals(DataSourceType.GLOBAL_STORAGE) || destination.equals(DataSourceType.GLOBAL_STORAGE)))
            return;

        if(source.equals(DataSourceType.LOCAL)){

            if(!pipelineManager.localCache.dataExist(dataClass, objectUUID))
                return;
            VCoreData data = pipelineManager.localCache.getData(dataClass, objectUUID);
            Map<String, Object> dataToSave = data.serialize();
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
                if (!pipelineManager.localCache.dataExist(dataClass, objectUUID))
                    pipelineManager.localCache.save(dataClass, pipelineManager.localCache.instantiateData(dataClass, objectUUID));
                VCoreData data = pipelineManager.localCache.getData(dataClass, objectUUID);
                data.deserialize(globalCachedData);
                data.onLoad();
            }
            else if(destination.equals(DataSourceType.GLOBAL_STORAGE))
                pipelineManager.globalStorage.save(dataClass, objectUUID, globalCachedData);
        }
        else if(source.equals(DataSourceType.GLOBAL_STORAGE)){
            if(!pipelineManager.globalStorage.dataExist(dataClass, objectUUID))
                return;
            Map<String, Object> globalSavedData = pipelineManager.globalStorage.loadData(dataClass, objectUUID);

            if(destination.equals(DataSourceType.LOCAL)) {
                if (!pipelineManager.localCache.dataExist(dataClass, objectUUID))
                    pipelineManager.localCache.save(dataClass, pipelineManager.localCache.instantiateData(dataClass, objectUUID));
                VCoreData data = pipelineManager.localCache.getData(dataClass, objectUUID);
                data.deserialize(globalSavedData);
                data.onLoad();
            }
            else if(destination.equals(DataSourceType.GLOBAL_CACHE))
                pipelineManager.globalCache.save(dataClass,objectUUID,globalSavedData);
        }
    }
}
