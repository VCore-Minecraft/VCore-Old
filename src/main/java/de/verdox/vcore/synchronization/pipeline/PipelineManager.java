/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline;

import de.verdox.vcore.synchronization.pipeline.annotations.DataContext;
import de.verdox.vcore.synchronization.pipeline.annotations.PreloadStrategy;
import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.parts.DataSynchronizer;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import de.verdox.vcore.synchronization.pipeline.parts.cache.GlobalCache;
import de.verdox.vcore.synchronization.pipeline.parts.local.LocalCache;
import de.verdox.vcore.synchronization.pipeline.parts.storage.GlobalStorage;
import de.verdox.vcore.plugin.SystemLoadable;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.06.2021 15:22
 */
public class PipelineManager implements Pipeline, SystemLoadable {

    public static final long EXPIRY_TIME_SECONDS = 60L * 1800;

    private final VCorePlugin<?, ?> plugin;
    private final DataSynchronizer pipelineDataSynchronizer;
    final GlobalStorage globalStorage;
    final GlobalCache globalCache;
    final LocalCache localCache;
    private boolean loaded;

    public PipelineManager(VCorePlugin<?,?> plugin, @Nonnull LocalCache localCache, @Nullable GlobalCache globalCache, @Nullable GlobalStorage globalStorage){
        this.plugin = plugin;
        this.globalStorage = globalStorage;
        this.globalCache = globalCache;
        this.localCache = localCache;
        this.pipelineDataSynchronizer = new PipelineDataSynchronizer(this);
        this.loaded = true;
        plugin.getScheduler().asyncInterval(() -> {

            plugin.getSubsystemManager().getRegisteredPlayerDataClasses().forEach(aClass -> {
                Set<UUID> cachedUUIDs = localCache.getSavedUUIDs(aClass);
                if(cachedUUIDs.isEmpty())
                    return;
                cachedUUIDs.forEach(uuid -> {
                    VCoreData data = localCache.getData(aClass, uuid);
                    if(!data.isExpired())
                        return;
                    data.save(true, false);
                    localCache.remove(aClass, uuid);
                });
            });
        }, 20L*10, 20L*500);
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
            T data = localCache.getData(type, uuid);
            data.updateLastUse();
            return data;
        }
        if(localCache.dataExist(type, uuid)) {
            T data = localCache.getData(type, uuid);
            data.updateLastUse();
            return data;
        }
        return loadFromPipeline(type, uuid, createIfNotExist);
    }

    @Override
    public <T extends VCoreData> Set<T> loadAllData(@Nonnull Class<? extends T> type, @Nonnull LoadingStrategy loadingStrategy) {
        Set<T> set = new HashSet<>();
        if(loadingStrategy.equals(LoadingStrategy.LOAD_PIPELINE))
            synchronizeData(type);
        else if(loadingStrategy.equals(LoadingStrategy.LOAD_LOCAL_ELSE_LOAD))
            plugin.async(() -> synchronizeData(type));
        getLocalCache().getSavedUUIDs(type).forEach(uuid -> set.add(getLocalCache().getData(type, uuid)));
        return set;
    }

    private <T extends VCoreData> void synchronizeData(@Nonnull Class<? extends T> type){
        if(getGlobalStorage() != null)
            getGlobalStorage().getSavedUUIDs(type).forEach(uuid -> {
                if(!localCache.dataExist(type, uuid))
                    pipelineDataSynchronizer.synchronize(DataSynchronizer.DataSourceType.GLOBAL_STORAGE, DataSynchronizer.DataSourceType.LOCAL, type, uuid);
            });
        if(getGlobalCache() != null)
            getGlobalCache().getSavedUUIDs(type).forEach(uuid -> {
                if(!localCache.dataExist(type, uuid))
                    pipelineDataSynchronizer.synchronize(DataSynchronizer.DataSourceType.GLOBAL_CACHE, DataSynchronizer.DataSourceType.LOCAL, type, uuid);
            });
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

    @Override
    public void saveAllData() {
        // getLocalCache().getSavedUUIDs()
        plugin.getSubsystemManager().getActiveServerDataClasses()
                .forEach(aClass -> getLocalCache().getSavedUUIDs(aClass).forEach(uuid -> getLocalCache().getData(aClass, uuid).save(true,false)));
        plugin.getSubsystemManager().getActivePlayerDataClasses()
                .forEach(aClass -> getLocalCache().getSavedUUIDs(aClass).forEach(uuid -> getLocalCache().getData(aClass, uuid).save(true, false)));
    }

    @Override
    public void preloadAllData(){
        plugin.getSubsystemManager().getActiveServerDataClasses().forEach(this::preloadData);
        plugin.getSubsystemManager().getActivePlayerDataClasses().forEach(this::preloadData);
    }

    @Override
    public DataSynchronizer getSynchronizer() {
        return pipelineDataSynchronizer;
    }

    <T extends VCoreData> T loadFromPipeline(@Nonnull Class<? extends T> dataClass, @Nonnull UUID uuid, boolean createIfNotExist){
        if(localCache.dataExist(dataClass,uuid)) {
            plugin.consoleMessage("&eFound Data in Local Cache &8[&b"+dataClass.getSimpleName()+"&8]", 1,true);
        }
        else if(globalCache != null && globalCache.dataExist(dataClass,uuid)) {
            plugin.consoleMessage("&eFound Data in Redis Cache &8[&b"+dataClass.getSimpleName()+"&8]", 1,true);
            pipelineDataSynchronizer.synchronize(DataSynchronizer.DataSourceType.GLOBAL_CACHE, DataSynchronizer.DataSourceType.LOCAL, dataClass, uuid);
            //getRedisHandler().redisToLocal(dataClass, uuid);
        }
        else if(globalStorage != null && globalStorage.dataExist(dataClass,uuid)) {
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
        T data = localCache.getData(dataClass, uuid);
        data.updateLastUse();
        return data;
    }

    private <S extends VCoreData> void preloadData(Class<? extends S> type){
        PreloadStrategy preloadStrategy = GlobalCache.getPreloadStrategy(type);
        if(!preloadStrategy.equals(PreloadStrategy.LOAD_BEFORE))
            return;
        if(globalCache != null)
            globalCache.getSavedUUIDs(type).forEach(uuid -> loadFromPipeline(type, uuid, false));
        if(globalStorage != null)
            globalStorage.getSavedUUIDs(type).forEach(uuid -> loadFromPipeline(type, uuid, false));
    }


    @Override
    public boolean isLoaded() {
        return loaded;
    }

    public VCorePlugin<?, ?> getPlugin() {
        return plugin;
    }
}
