/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.pipeline;

import de.verdox.vcore.pipeline.annotations.DataContext;
import de.verdox.vcore.data.manager.LoadingStrategy;
import de.verdox.vcore.pipeline.datatypes.VCoreData;
import de.verdox.vcore.pipeline.parts.DataSynchronizer;
import de.verdox.vcore.pipeline.parts.Pipeline;
import de.verdox.vcore.pipeline.parts.cache.GlobalCache;
import de.verdox.vcore.pipeline.parts.local.LocalCache;
import de.verdox.vcore.pipeline.parts.storage.GlobalStorage;
import de.verdox.vcore.plugin.SystemLoadable;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.06.2021 15:22
 */
public class PipelineManager implements Pipeline, SystemLoadable {

    private final VCorePlugin<?, ?> plugin;
    private final DataSynchronizer pipelineDataSynchronizer;
    final GlobalStorage globalStorage;
    final GlobalCache globalCache;
    final LocalCache localCache;
    private boolean loaded;

    public PipelineManager(VCorePlugin<?,?> plugin, GlobalStorage globalStorage, GlobalCache globalCache, LocalCache localCache){
        this.plugin = plugin;
        this.globalStorage = globalStorage;
        this.globalCache = globalCache;
        this.localCache = localCache;
        this.pipelineDataSynchronizer = new PipelineDataSynchronizer(this);
        this.loaded = true;
    }

    @Override
    public final <T extends VCoreData> T load(@Nonnull Class<? extends T> type, @Nonnull UUID uuid, @Nonnull LoadingStrategy loadingStrategy, boolean createIfNotExist){
        return load(type, uuid, loadingStrategy, createIfNotExist, null);
    }
    @Override
    public final <T extends VCoreData> T load(@Nonnull Class<? extends T> type, @Nonnull UUID uuid, @Nonnull LoadingStrategy loadingStrategy, @Nullable Consumer<T> callback){
        return load(type, uuid, loadingStrategy, false, callback);
    }
    @Override
    public final <T extends VCoreData> T load(@Nonnull Class<? extends T> type, @Nonnull UUID uuid, @Nonnull LoadingStrategy loadingStrategy){
        return load(type, uuid, loadingStrategy, null);
    }

    /**
     * @param type The type you want to load
     * @param uuid The uuid ob the object you want to load
     * @param loadingStrategy The LoadingStrategy you prefer
     * @param callback If you choose LoadingStrategy.LOAD_LOCAL_ELSE_LOAD a callback can be executed
     * @param <T> Type of VCoreData
     * @return VCoreDataType
     */
    @Override
    public final <T extends VCoreData> T load(@Nonnull Class<? extends T> type, @Nonnull UUID uuid, @Nonnull LoadingStrategy loadingStrategy, boolean createIfNotExist, @Nullable Consumer<T> callback){
        VCoreSubsystem<?> subsystem = plugin.findDependSubsystem(type);
        if(subsystem == null)
            throw new NullPointerException("Subsystem of "+type+" could not be found in plugin"+plugin.getPluginName());
        if(!loadingStrategy.equals(LoadingStrategy.LOAD_PIPELINE)) {
            if(!localCache.dataExist(type, uuid)){
                if(loadingStrategy.equals(LoadingStrategy.LOAD_LOCAL))
                    throw new NullPointerException(type+" with uuid "+uuid+" does not exist in local!");
                else{
                    plugin.async(() -> {
                        T data = loadFromPipeline(type, uuid, createIfNotExist);
                        if(callback != null)
                            callback.accept(data);
                    });
                }
            }
            if(!localCache.dataExist(type, uuid))
                return null;
            return localCache.getData(type,uuid);
        }
        return loadFromPipeline(type, uuid, createIfNotExist);
    }

    @Override
    public LocalCache getLocalCache() {
        return localCache;
    }

    @Override
    public GlobalCache getGlobalCache() {
        return globalCache;
    }

    @Override
    public GlobalStorage getGlobalStorage() {
        return globalStorage;
    }

    private <T extends VCoreData> T loadFromPipeline(@Nonnull Class<? extends T> dataClass, @Nonnull UUID uuid, boolean createIfNotExist){
        if(localCache.dataExist(dataClass,uuid)) {
            plugin.consoleMessage("&eFound Data in Local Cache &8[&b"+dataClass.getSimpleName()+"&8]", 1,true);
        }
        else if(globalCache.dataExist(dataClass,uuid)) {
            plugin.consoleMessage("&eFound Data in Redis Cache &8[&b"+dataClass.getSimpleName()+"&8]", 1,true);
            pipelineDataSynchronizer.synchronize(DataSynchronizer.DataSourceType.GLOBAL_CACHE, DataSynchronizer.DataSourceType.LOCAL, dataClass, uuid);
            //getRedisHandler().redisToLocal(dataClass, uuid);
        }
        else if(globalStorage.dataExist(dataClass,uuid)) {
            plugin.consoleMessage("&eFound Data in Database &8[&b"+dataClass.getSimpleName()+"&8]", 1,true);
            //getDatabaseHandler().databaseToLocal(dataClass,uuid);
            pipelineDataSynchronizer.synchronize(DataSynchronizer.DataSourceType.GLOBAL_STORAGE, DataSynchronizer.DataSourceType.LOCAL, dataClass, uuid);

            if(GlobalCache.getContext(dataClass).equals(DataContext.GLOBAL))
                //globalStorage.dataBaseToRedis(dataClass, uuid);
                pipelineDataSynchronizer.synchronize(DataSynchronizer.DataSourceType.GLOBAL_STORAGE, DataSynchronizer.DataSourceType.GLOBAL_CACHE, dataClass, uuid);
        }
        else {
            if(!createIfNotExist)
                return null;
            plugin.consoleMessage("&eNo Data was found. Creating new data! &8[&b"+dataClass.getSimpleName()+"&8]", 1,true);
            T vCoreData = localCache.instantiateData(dataClass,uuid);
            //TODO: Push Mechanik irgendwie einbauen
            localCache.save(dataClass, vCoreData);
            //getLocalDataHandler().localToRedis(vCoreData,dataClass,vCoreData.getUUID());
            pipelineDataSynchronizer.synchronize(DataSynchronizer.DataSourceType.LOCAL, DataSynchronizer.DataSourceType.GLOBAL_CACHE, dataClass, uuid);
        }
        plugin.consoleMessage("&eLoaded &a"+dataClass.getSimpleName()+" &ewith uuid&7: "+uuid, 1,true);
        if(!localCache.dataExist(dataClass, uuid))
            throw new NullPointerException("Error in dataPipeline while loading "+dataClass+" with uuid "+uuid);
        return localCache.getData(dataClass,uuid);
    }


    @Override
    public boolean isLoaded() {
        return loaded;
    }
}
