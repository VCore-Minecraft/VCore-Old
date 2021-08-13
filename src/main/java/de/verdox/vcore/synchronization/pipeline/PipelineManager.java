/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline;

import de.verdox.vcore.performance.concurrent.CatchingRunnable;
import de.verdox.vcore.synchronization.pipeline.annotations.DataContext;
import de.verdox.vcore.synchronization.pipeline.annotations.PreloadStrategy;
import de.verdox.vcore.synchronization.pipeline.annotations.VCoreDataContext;
import de.verdox.vcore.synchronization.pipeline.datatypes.NetworkData;
import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.parts.DataSynchronizer;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import de.verdox.vcore.synchronization.pipeline.parts.cache.GlobalCache;
import de.verdox.vcore.synchronization.pipeline.parts.local.LocalCache;
import de.verdox.vcore.synchronization.pipeline.parts.storage.GlobalStorage;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import de.verdox.vcore.synchronization.pipeline.parts.storage.PipelineTaskScheduler;
import io.netty.util.concurrent.DefaultThreadFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.06.2021 15:22
 */
public class PipelineManager implements Pipeline {

    private final VCorePlugin<?, ?> plugin;
    private final PipelineDataSynchronizer pipelineDataSynchronizer;
    final GlobalStorage globalStorage;
    final GlobalCache globalCache;
    final LocalCache localCache;
    private ExecutorService executorService;
    private boolean loaded;
    private final PipelineTaskScheduler pipelineTaskScheduler;

    public PipelineManager(VCorePlugin<?,?> plugin, @Nonnull LocalCache localCache, @Nullable GlobalCache globalCache, @Nullable GlobalStorage globalStorage){
        this.plugin = plugin;
        this.executorService  = Executors.newFixedThreadPool(2,new DefaultThreadFactory(plugin.getPluginName()+"Pipeline"));
        this.globalStorage = globalStorage;
        this.globalCache = globalCache;
        this.localCache = localCache;
        plugin.consoleMessage("&6Starting Pipeline Manager", false);
        plugin.consoleMessage("&eLocalCache: &b"+localCache, 1,false);
        plugin.consoleMessage("&eGlobalCache: &b"+globalCache, 1,false);
        plugin.consoleMessage("&eGlobalStorage: &b"+globalStorage, 1,false);
        this.pipelineTaskScheduler = new PipelineTaskSchedulerImpl(this);
        this.pipelineDataSynchronizer = new PipelineDataSynchronizer(this);
        plugin.getServices().getVCoreScheduler().asyncInterval(() -> {
            plugin.getServices().getSubsystemManager().getRegisteredPlayerDataClasses().forEach(aClass -> {
                VCoreDataContext vCoreDataContext = GlobalCache.getDataContext(aClass);
                if(vCoreDataContext == null)
                    return;
                if(!vCoreDataContext.cleanOnNoUse())
                    return;
                Set<UUID> cachedUUIDs = localCache.getSavedUUIDs(aClass);
                if(cachedUUIDs.isEmpty())
                    return;
                cachedUUIDs.forEach(uuid -> {
                    VCoreData data = localCache.getData(aClass, uuid);
                    data.save(true);
                    // If Player is online don't unload
                    if(plugin.getPlatformWrapper().isPlayerOnline(data.getObjectUUID()))
                        return;
                    if(!data.isExpired())
                        return;
                    data.cleanUp();
                    data.save(true);
                    localCache.remove(aClass, uuid);
                });
            });
            plugin.getServices().getSubsystemManager().getRegisteredServerDataClasses().forEach(aClass -> {
                VCoreDataContext vCoreDataContext = GlobalCache.getDataContext(aClass);
                if(vCoreDataContext == null)
                    return;
                if(!vCoreDataContext.cleanOnNoUse())
                    return;
                Set<UUID> cachedUUIDs = localCache.getSavedUUIDs(aClass);
                if(cachedUUIDs.isEmpty())
                    return;
                cachedUUIDs.forEach(uuid -> {
                    VCoreData data = localCache.getData(aClass, uuid);
                    data.save(true);
                    if(!data.isExpired())
                        return;
                    data.cleanUp();
                    data.save(true);
                    localCache.remove(aClass, uuid);
                });
            });
        }, 20L*10, 20L*300);
        loaded = true;
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
        plugin.consoleMessage("&8[&e"+loadingStrategy+"&8] &bLoading data from pipeline &a"+type.getSimpleName()+" &b"+uuid,true);
        PipelineTaskScheduler.PipelineTask<T> pipelineTask = pipelineTaskScheduler.schedulePipelineTask(PipelineTaskScheduler.PipelineAction.LOAD, loadingStrategy, type, uuid);

        if(!NetworkData.class.isAssignableFrom(type)){
            VCoreSubsystem<?> subsystem = plugin.findDependSubsystem(type);
            if(subsystem == null)
                throw new NullPointerException("Subsystem of "+type+" could not be found in plugin"+plugin.getPluginName());
        }
        if(!loadingStrategy.equals(LoadingStrategy.LOAD_PIPELINE)) {
            if(!localCache.dataExist(type, uuid)){
                if(loadingStrategy.equals(LoadingStrategy.LOAD_LOCAL))
                    throw new NullPointerException(type+" with uuid "+uuid+" does not exist in local!");
                else{
                    // LOAD_LOCAL_ELSE_LOAD
                    executorService.submit(new CatchingRunnable(() -> {
                        T data = loadFromPipeline(type, uuid, createIfNotExist);
                        pipelineTask.getCompletableFuture().complete(data);
                        plugin.consoleMessage("&8[&e"+loadingStrategy+"&8] &eCompleted with&7: &b "+data,true);
                        if(callback != null)
                            callback.accept(data);
                    }));
                }
            }
            if(!localCache.dataExist(type, uuid)){
                throw new IllegalStateException("Does not exist in Local Cache");
            }
            T data = localCache.getData(type, uuid);
            if(callback != null)
                callback.accept(data);
            data.updateLastUse();
            pipelineTask.getCompletableFuture().complete(data);
            plugin.consoleMessage("&8[&e"+loadingStrategy+"&8] &eCompleted with&7: &b "+data,true);
            return data;
        }
        if(localCache.dataExist(type, uuid)) {
            T data = localCache.getData(type, uuid);
            data.updateLastUse();
            pipelineTask.getCompletableFuture().complete(data);
            plugin.consoleMessage("&8[&e"+loadingStrategy+"&8] &eCompleted with&7: &b "+data,true);
            if(callback != null)
                callback.accept(data);
            return data;
        }
        T data = loadFromPipeline(type, uuid, createIfNotExist);
        pipelineTask.getCompletableFuture().complete(data);
        plugin.consoleMessage("&8[&e"+loadingStrategy+"&8] &eCompleted with&7: &b "+data,true);
        return data;
    }

    @Override
    public <T extends VCoreData> CompletableFuture<T> loadAsync(@Nonnull Class<? extends T> type, @Nonnull UUID uuid, @Nonnull LoadingStrategy loadingStrategy, boolean createIfNotExist, @org.jetbrains.annotations.Nullable Consumer<T> callback) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        executorService.submit(new CatchingRunnable(() -> completableFuture.complete(load(type, uuid, loadingStrategy, createIfNotExist, callback))));
        return completableFuture;
    }

    @Override
    public <T extends VCoreData> Set<T> loadAllData(@Nonnull Class<? extends T> type, @Nonnull LoadingStrategy loadingStrategy) {
        Set<T> set = new HashSet<>();
        if(loadingStrategy.equals(LoadingStrategy.LOAD_PIPELINE))
            synchronizeData(type);
        else if(loadingStrategy.equals(LoadingStrategy.LOAD_LOCAL_ELSE_LOAD))
            executorService.submit(new CatchingRunnable(() -> synchronizeData(type)));
        getLocalCache().getSavedUUIDs(type).forEach(uuid -> set.add(getLocalCache().getData(type, uuid)));
        return set;
    }

    @Override
    public <T extends VCoreData> CompletableFuture<Set<T>> loadAllDataAsync(@Nonnull Class<? extends T> type, @Nonnull LoadingStrategy loadingStrategy) {
        CompletableFuture<Set<T>> completableFuture = new CompletableFuture<>();
        executorService.submit(new CatchingRunnable(() -> completableFuture.complete(loadAllData(type,loadingStrategy))));
        return completableFuture;
    }

    @Override
    public <T extends VCoreData> boolean exist(@Nonnull Class<? extends T> type, @Nonnull UUID uuid, @Nonnull QueryStrategy... strategies) {
        if(strategies.length == 0)
            return false;
        for (QueryStrategy queryStrategy : Arrays.stream(strategies).collect(Collectors.toSet())) {
            if(queryStrategy.equals(QueryStrategy.LOCAL)){
                boolean localExist = getLocalCache().dataExist(type, uuid);
                if(localExist)
                    return true;
            }
            else if(queryStrategy.equals(QueryStrategy.GLOBAL_CACHE)){
                if(getGlobalCache() == null)
                    continue;
                boolean globalCacheExists = getGlobalCache().dataExist(type, uuid);
                if(globalCacheExists)
                    return true;
            }
            else if(queryStrategy.equals(QueryStrategy.GLOBAL_STORAGE)){
                if(getGlobalStorage() == null)
                    continue;
                boolean globalStorageExists = getGlobalStorage().dataExist(type, uuid);
                if(globalStorageExists)
                    return true;
            }
        }
        return false;
    }

    @Override
    public <T extends VCoreData> CompletableFuture<Boolean> existAsync(@Nonnull Class<? extends T> type, @Nonnull UUID uuid, @Nonnull QueryStrategy... strategies) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        executorService.submit(new CatchingRunnable(() -> completableFuture.complete(exist(type, uuid, strategies))));
        return completableFuture;
    }

    @Override
    public <T extends VCoreData> boolean delete(@Nonnull Class<? extends T> type, @Nonnull UUID uuid) {
        plugin.consoleMessage("&eDeleting&7: &b"+type.getSimpleName()+" &ewith uuid &a"+uuid,true);
        if(getLocalCache() != null)
            if(!getLocalCache().remove(type, uuid))
                plugin.consoleMessage("&8[&eLocalCache&8] &cCould not delete&7: &b"+type.getSimpleName()+" &ewith uuid &a"+uuid,true);
        if(getGlobalCache() != null)
            if(!getGlobalCache().remove(type,uuid))
                plugin.consoleMessage("&8[&eGlobalCache&8] &cCould not delete&7: &b"+type.getSimpleName()+" &ewith uuid &a"+uuid,true);
        if(getGlobalStorage() != null)
            if(!getGlobalStorage().remove(type,uuid))
                plugin.consoleMessage("&8[&eGlobalStorage&8] &cCould not delete&7: &b"+type.getSimpleName()+" &ewith uuid &a"+uuid,true);
        return true;
    }

    @Override
    public <T extends VCoreData> CompletableFuture<Boolean> deleteAsync(@Nonnull Class<? extends T> type, @Nonnull UUID uuid) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        executorService.submit(new CatchingRunnable(() -> completableFuture.complete(delete(type, uuid))));
        return completableFuture;
    }

    private <T extends VCoreData> void synchronizeData(@Nonnull Class<? extends T> type){
        if(getGlobalStorage() != null)
            getGlobalStorage().getSavedUUIDs(type).forEach(uuid -> {
                if(!localCache.dataExist(type, uuid))
                    pipelineDataSynchronizer.doSynchronisation(DataSynchronizer.DataSourceType.GLOBAL_STORAGE, DataSynchronizer.DataSourceType.LOCAL, type, uuid, null);
            });
        if(getGlobalCache() != null)
            getGlobalCache().getSavedUUIDs(type).forEach(uuid -> {
                if(!localCache.dataExist(type, uuid))
                    pipelineDataSynchronizer.doSynchronisation(DataSynchronizer.DataSourceType.GLOBAL_CACHE, DataSynchronizer.DataSourceType.LOCAL, type, uuid, null);
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
        plugin.getServices().getSubsystemManager().getActiveServerDataClasses()
                .forEach(aClass -> getLocalCache().getSavedUUIDs(aClass).forEach(uuid -> {
                    VCoreData vCoreData = getLocalCache().getData(aClass,uuid);
                    vCoreData.cleanUp();
                    pipelineDataSynchronizer.doSynchronisation(DataSynchronizer.DataSourceType.LOCAL, DataSynchronizer.DataSourceType.GLOBAL_CACHE,aClass,uuid,null);
                    pipelineDataSynchronizer.doSynchronisation(DataSynchronizer.DataSourceType.LOCAL, DataSynchronizer.DataSourceType.GLOBAL_STORAGE,aClass,uuid,null);
                    vCoreData.save(false);
                    getLocalCache().remove(aClass,uuid);
                    plugin.consoleMessage("&aSaved &b"+uuid+" &8[&e"+aClass+"&8]",false);
                }));
        plugin.getServices().getSubsystemManager().getActivePlayerDataClasses()
                .forEach(aClass -> getLocalCache().getSavedUUIDs(aClass).forEach(uuid -> {
                    VCoreData vCoreData = getLocalCache().getData(aClass,uuid);
                    vCoreData.cleanUp();
                    pipelineDataSynchronizer.doSynchronisation(DataSynchronizer.DataSourceType.LOCAL, DataSynchronizer.DataSourceType.GLOBAL_CACHE,aClass,uuid,null);
                    pipelineDataSynchronizer.doSynchronisation(DataSynchronizer.DataSourceType.LOCAL, DataSynchronizer.DataSourceType.GLOBAL_STORAGE,aClass,uuid,null);
                    vCoreData.save(false);
                    plugin.consoleMessage("&aSaved &b"+uuid+" &8[&e"+aClass+"&8]",false);
                    getLocalCache().remove(aClass,uuid);
                }));
    }

    @Override
    public void preloadAllData(){
        plugin.getServices().getSubsystemManager().getActiveServerDataClasses().forEach(this::preloadData);
        plugin.getServices().getSubsystemManager().getActivePlayerDataClasses().forEach(this::preloadData);
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
            pipelineDataSynchronizer.doSynchronisation(DataSynchronizer.DataSourceType.GLOBAL_CACHE, DataSynchronizer.DataSourceType.LOCAL, dataClass, uuid, null);
            //getRedisHandler().redisToLocal(dataClass, uuid);
        }
        else if(globalStorage != null && globalStorage.dataExist(dataClass,uuid)) {
            plugin.consoleMessage("&eFound Data in Database &8[&b"+dataClass.getSimpleName()+"&8]", 1,true);
            //getDatabaseHandler().databaseToLocal(dataClass,uuid);
            pipelineDataSynchronizer.doSynchronisation(DataSynchronizer.DataSourceType.GLOBAL_STORAGE, DataSynchronizer.DataSourceType.LOCAL, dataClass, uuid, null);

            if(GlobalCache.getContext(dataClass).equals(DataContext.GLOBAL))
                //globalStorage.dataBaseToRedis(dataClass, uuid);
                pipelineDataSynchronizer.doSynchronisation(DataSynchronizer.DataSourceType.GLOBAL_STORAGE, DataSynchronizer.DataSourceType.GLOBAL_CACHE, dataClass, uuid, null);
        }
        else {
            if(!createIfNotExist)
                return null;
            plugin.consoleMessage("&eNo Data was found. Creating new data! &8[&b"+dataClass.getSimpleName()+"&8]", 1,true);
            T vCoreData = localCache.instantiateData(dataClass,uuid);
            vCoreData.onCreate();
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
            globalCache.getSavedUUIDs(type).forEach(uuid -> pipelineDataSynchronizer.doSynchronisation(DataSynchronizer.DataSourceType.GLOBAL_CACHE, DataSynchronizer.DataSourceType.LOCAL, type, uuid, null));
        if(globalStorage != null)
            globalStorage.getSavedUUIDs(type).forEach(uuid -> pipelineDataSynchronizer.doSynchronisation(DataSynchronizer.DataSourceType.GLOBAL_STORAGE, DataSynchronizer.DataSourceType.LOCAL, type, uuid, null));
    }


    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void shutdown() {
        pipelineDataSynchronizer.shutdown();
        executorService.shutdown();
        pipelineTaskScheduler.shutdown();
        try { executorService.awaitTermination(5, TimeUnit.SECONDS); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public VCorePlugin<?, ?> getPlugin() {
        return plugin;
    }
}
