/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline;

import de.verdox.vcore.performance.concurrent.CatchingRunnable;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import de.verdox.vcore.synchronization.pipeline.annotations.DataContext;
import de.verdox.vcore.synchronization.pipeline.annotations.PreloadStrategy;
import de.verdox.vcore.synchronization.pipeline.annotations.VCoreDataProperties;
import de.verdox.vcore.synchronization.pipeline.datatypes.NetworkData;
import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.parts.DataSynchronizer;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import de.verdox.vcore.synchronization.pipeline.parts.cache.GlobalCache;
import de.verdox.vcore.synchronization.pipeline.parts.local.LocalCache;
import de.verdox.vcore.synchronization.pipeline.parts.storage.GlobalStorage;
import de.verdox.vcore.synchronization.pipeline.parts.storage.PipelineTaskScheduler;
import de.verdox.vcore.util.global.AnnotationResolver;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.06.2021 15:22
 */
public class PipelineManager implements Pipeline {

    final GlobalStorage globalStorage;
    final GlobalCache globalCache;
    final LocalCache localCache;
    private final VCorePlugin<?, ?> plugin;
    private final PipelineDataSynchronizer pipelineDataSynchronizer;
    private final PipelineTaskScheduler pipelineTaskScheduler;
    private final ExecutorService executorService;
    private final boolean loaded;

    public PipelineManager(VCorePlugin<?, ?> plugin, @NotNull LocalCache localCache, @Nullable GlobalCache globalCache, @Nullable GlobalStorage globalStorage) {
        this.plugin = plugin;
        this.executorService = Executors.newFixedThreadPool(2, new DefaultThreadFactory(plugin.getPluginName() + "Pipeline"));
        this.globalStorage = globalStorage;
        this.globalCache = globalCache;
        this.localCache = localCache;
        plugin.consoleMessage("&6Starting Pipeline Manager", false);
        plugin.consoleMessage("&eLocalCache: &b" + localCache, 1, false);
        plugin.consoleMessage("&eGlobalCache: &b" + globalCache, 1, false);
        plugin.consoleMessage("&eGlobalStorage: &b" + globalStorage, 1, false);
        this.pipelineTaskScheduler = new PipelineTaskSchedulerImpl(this);
        this.pipelineDataSynchronizer = new PipelineDataSynchronizer(this);
        plugin.getServices().getVCoreScheduler().asyncInterval(() -> {
            plugin.getServices().getSubsystemManager().getRegisteredPlayerDataClasses().forEach(aClass -> {
                VCoreDataProperties vCoreDataProperties = AnnotationResolver.getDataProperties(aClass);
                if (!vCoreDataProperties.cleanOnNoUse())
                    return;
                Set<UUID> cachedUUIDs = localCache.getSavedUUIDs(aClass);
                if (cachedUUIDs.isEmpty())
                    return;
                cachedUUIDs.forEach(uuid -> {
                    VCoreData data = localCache.getData(aClass, uuid);
                    data.save(true);
                    // If Player is online don't unload
                    if (plugin.getPlatformWrapper().isPlayerOnline(data.getObjectUUID()))
                        return;
                    if (!data.isExpired())
                        return;
                    data.cleanUp();
                    data.save(true);
                    localCache.remove(aClass, uuid);
                });
            });
            plugin.getServices().getSubsystemManager().getRegisteredServerDataClasses().forEach(aClass -> {
                VCoreDataProperties vCoreDataProperties = AnnotationResolver.getDataProperties(aClass);
                if (!vCoreDataProperties.cleanOnNoUse())
                    return;
                Set<UUID> cachedUUIDs = localCache.getSavedUUIDs(aClass);
                if (cachedUUIDs.isEmpty())
                    return;
                cachedUUIDs.forEach(uuid -> {
                    VCoreData data = localCache.getData(aClass, uuid);
                    data.save(true);
                    if (!data.isExpired())
                        return;
                    data.cleanUp();
                    data.save(true);
                    localCache.remove(aClass, uuid);
                });
            });
        }, 20L * 10, 20L * 300);
        loaded = true;
    }

    /**
     * @param type            The type you want to load
     * @param uuid            The uuid ob the object you want to load
     * @param loadingStrategy The LoadingStrategy you prefer
     * @param callback        If you choose LoadingStrategy.LOAD_LOCAL_ELSE_LOAD a callback can be executed
     * @param <T>             Type of VCoreData
     * @return VCoreDataType
     */
    @Override
    public final <T extends VCoreData> T load(@NotNull Class<? extends T> type, @NotNull(exception = NullPointerException.class) UUID uuid, @NotNull LoadingStrategy loadingStrategy, boolean createIfNotExist, @Nullable Consumer<T> callback) {
        //plugin.consoleMessage("&8[&e" + loadingStrategy + "&8] &bLoading data from pipeline &a" + type.getSimpleName() + " &b" + uuid, true);
        PipelineTaskScheduler.PipelineTask<T> pipelineTask = pipelineTaskScheduler.schedulePipelineTask(PipelineTaskScheduler.PipelineAction.LOAD, loadingStrategy, type, uuid);

        // Subsystem Check
        if (!NetworkData.class.isAssignableFrom(type)) {
            VCoreSubsystem<?> subsystem = plugin.findDependSubsystem(type);
            if (subsystem == null) {
                plugin.consoleMessage("&4You are trying to load &e" + type + " &4with a Pipeline that does not know this type. Choose the Plugin that knows the subsystem of this dataType to prevent this error.", false);
                pipelineTask.getCompletableFuture().complete(null);
                throw new IllegalStateException("Subsystem of " + type + " could not be found in plugin" + plugin.getPluginName());
            }
        }

        if (localCache.dataExist(type, uuid)) {
            T data = localCache.getData(type, uuid);
            if (callback != null)
                callback.accept(data);
            data.updateLastUse();
            pipelineTask.getCompletableFuture().complete(data);
            return data;
        } else if (loadingStrategy.equals(LoadingStrategy.LOAD_LOCAL)) {
            if (createIfNotExist) {
                T data = createNewData(type, uuid);
                data.updateLastUse();
                pipelineTask.getCompletableFuture().complete(data);
                return data;
            }
            pipelineTask.getCompletableFuture().complete(null);
            throw new NullPointerException(type + " with uuid " + uuid + " does not exist in local!");
        } else if (loadingStrategy.equals(LoadingStrategy.LOAD_LOCAL_ELSE_LOAD)) {
            executorService.submit(new CatchingRunnable(() -> {
                T data = loadFromPipeline(type, uuid, createIfNotExist);
                pipelineTask.getCompletableFuture().complete(data);
                plugin.consoleMessage("&8[&e" + loadingStrategy + "&8] &eCompleted with&7: &b " + data, true);
                if (callback != null)
                    callback.accept(data);
            }));
            return null;
        } else if (loadingStrategy.equals(LoadingStrategy.LOAD_PIPELINE)) {
            T data = loadFromPipeline(type, uuid, createIfNotExist);
            pipelineTask.getCompletableFuture().complete(data);
            plugin.consoleMessage("&8[&e" + loadingStrategy + "&8] &eCompleted with&7: &b " + data, true);
            return data;
        }
        return null;
    }

    @NotNull
    @Override
    public <T extends VCoreData> CompletableFuture<T> loadAsync(@NotNull Class<? extends T> type, @NotNull UUID uuid, @NotNull LoadingStrategy loadingStrategy, boolean createIfNotExist, @org.jetbrains.annotations.Nullable Consumer<T> callback) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        executorService.submit(new CatchingRunnable(() -> completableFuture.complete(load(type, uuid, loadingStrategy, createIfNotExist, callback))));
        return completableFuture;
    }

    @NotNull
    @Override
    public <T extends VCoreData> Set<T> loadAllData(@NotNull Class<? extends T> type, @NotNull LoadingStrategy loadingStrategy) {
        Set<T> set = new HashSet<>();
        if (loadingStrategy.equals(LoadingStrategy.LOAD_PIPELINE))
            synchronizeData(type);
        else if (loadingStrategy.equals(LoadingStrategy.LOAD_LOCAL_ELSE_LOAD))
            executorService.submit(new CatchingRunnable(() -> synchronizeData(type)));
        getLocalCache().getSavedUUIDs(type).forEach(uuid -> set.add(getLocalCache().getData(type, uuid)));
        return set;
    }

    @NotNull
    @Override
    public <T extends VCoreData> CompletableFuture<Set<T>> loadAllDataAsync(@NotNull Class<? extends T> type, @NotNull LoadingStrategy loadingStrategy) {
        CompletableFuture<Set<T>> completableFuture = new CompletableFuture<>();
        executorService.submit(new CatchingRunnable(() -> completableFuture.complete(loadAllData(type, loadingStrategy))));
        return completableFuture;
    }

    @Override
    public <T extends VCoreData> boolean exist(@NotNull Class<? extends T> type, @NotNull UUID uuid, @NotNull QueryStrategy... strategies) {
        if (strategies.length == 0)
            return false;
        Set<QueryStrategy> strategySet = Arrays.stream(strategies).collect(Collectors.toSet());

        if (strategySet.contains(QueryStrategy.ALL) || strategySet.contains(QueryStrategy.LOCAL)) {
            boolean localExist = getLocalCache().dataExist(type, uuid);
            if (localExist)
                return true;
        }
        if (strategySet.contains(QueryStrategy.ALL) || strategySet.contains(QueryStrategy.GLOBAL_CACHE)) {
            if (getGlobalCache() != null) {
                boolean globalCacheExists = getGlobalCache().dataExist(type, uuid);
                if (globalCacheExists)
                    return true;
            }
        }
        if (strategySet.contains(QueryStrategy.ALL) || strategySet.contains(QueryStrategy.GLOBAL_STORAGE)) {
            if (getGlobalStorage() != null)
                return getGlobalStorage().dataExist(type, uuid);
        }
        return false;
    }

    @Override
    public <T extends VCoreData> CompletableFuture<Boolean> existAsync(@NotNull Class<? extends T> type, @NotNull UUID uuid, @NotNull QueryStrategy... strategies) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        executorService.submit(new CatchingRunnable(() -> completableFuture.complete(exist(type, uuid, strategies))));
        return completableFuture;
    }

    @Override
    public <T extends VCoreData> boolean delete(@NotNull Class<? extends T> type, @NotNull UUID uuid, boolean notifyOthers, @NotNull QueryStrategy... strategies) {
        Set<QueryStrategy> strategySet = Arrays.stream(strategies).collect(Collectors.toSet());
        if (strategySet.isEmpty())
            strategySet.add(QueryStrategy.ALL);
        plugin.consoleMessage("&eDeleting&7: &b" + type.getSimpleName() + " &euuid &a" + uuid + "&e" + Arrays.toString(strategies), true);
        if (strategySet.contains(QueryStrategy.ALL) || strategySet.contains(QueryStrategy.LOCAL)) {
            plugin.consoleMessage("&eDeleting from Local Cache&7: &b" + type.getSimpleName() + " &euuid &a" + uuid + "&e", true);
            T data = getLocalCache().getData(type, uuid);

            if (data != null)
                data.onDelete();

            if (!getLocalCache().remove(type, uuid))
                plugin.consoleMessage("&8[&eLocalCache&8] &cCould not delete&7: &b" + type.getSimpleName() + " &euuid &a" + uuid, true);
            else if (data != null) {
                if (notifyOthers)
                    data.getDataManipulator().pushRemoval(data, null);
                data.markForRemoval();
                plugin.consoleMessage("&8[&eLocalCache&8] &eDeleted&7: &b" + type.getSimpleName() + " &euuid &a" + uuid + "&e" + Arrays.toString(strategies), true);
            }
        }
        if (getGlobalCache() != null && (strategySet.contains(QueryStrategy.ALL) || strategySet.contains(QueryStrategy.GLOBAL_CACHE))) {
            plugin.consoleMessage("&eDeleting from Global Cache&7: &b" + type.getSimpleName() + " &euuid &a" + uuid + "&e", true);
            if (!getGlobalCache().remove(type, uuid))
                plugin.consoleMessage("&8[&eGlobalCache&8] &cCould not delete&7: &b" + type.getSimpleName() + " &euuid &a" + uuid, true);
            else
                plugin.consoleMessage("&8[&eGlobalCache&8] &eDeleted&7: &b" + type.getSimpleName() + " &euuid &a" + uuid + "&e" + Arrays.toString(strategies), true);
        }
        if (getGlobalStorage() != null && (strategySet.contains(QueryStrategy.ALL) || strategySet.contains(QueryStrategy.GLOBAL_STORAGE))) {
            plugin.consoleMessage("&eDeleting from Global Storage&7: &b" + type.getSimpleName() + " &euuid &a" + uuid + "&e", true);
            if (!getGlobalStorage().remove(type, uuid))
                plugin.consoleMessage("&8[&eGlobalStorage&8] &cCould not delete&7: &b" + type.getSimpleName() + " &euuid &a" + uuid, true);
            else
                plugin.consoleMessage("&8[&eGlobalStorage&8] &eDeleted&7: &b" + type.getSimpleName() + " &euuid &a" + uuid + "&e" + Arrays.toString(strategies), true);
        }
        return true;
    }

    @Override
    public <T extends VCoreData> CompletableFuture<Boolean> deleteAsync(@NotNull Class<? extends T> type, @NotNull UUID uuid, boolean notifyOthers, @NotNull QueryStrategy... strategies) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        executorService.submit(new CatchingRunnable(() -> completableFuture.complete(delete(type, uuid, notifyOthers, strategies))));
        return completableFuture;
    }

    private <T extends VCoreData> void synchronizeData(@NotNull Class<? extends T> type) {
        if (getGlobalStorage() != null)
            getGlobalStorage().getSavedUUIDs(type).forEach(uuid -> {
                if (!localCache.dataExist(type, uuid))
                    pipelineDataSynchronizer.doSynchronisation(DataSynchronizer.DataSourceType.GLOBAL_STORAGE, DataSynchronizer.DataSourceType.LOCAL, type, uuid, null);
            });
        if (getGlobalCache() != null)
            getGlobalCache().getSavedUUIDs(type).forEach(uuid -> {
                if (!localCache.dataExist(type, uuid))
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
        plugin.consoleMessage("&eSaving all data&7...", false);
        // getLocalCache().getSavedUUIDs()
        plugin.getServices().getSubsystemManager().getActiveServerDataClasses()
                .forEach(aClass -> getLocalCache().getSavedUUIDs(aClass).forEach(uuid -> saveData(aClass, uuid)));
        plugin.getServices().getSubsystemManager().getActivePlayerDataClasses()
                .forEach(aClass -> getLocalCache().getSavedUUIDs(aClass).forEach(uuid -> saveData(aClass, uuid)));
    }

    private void saveData(@NotNull Class<? extends VCoreData> type, @NotNull UUID uuid) {
        VCoreData vCoreData = getLocalCache().getData(type, uuid);
        if (vCoreData == null)
            return;
        if (vCoreData.isMarkedForRemoval())
            return;
        vCoreData.cleanUp();
        pipelineDataSynchronizer.doSynchronisation(DataSynchronizer.DataSourceType.LOCAL, DataSynchronizer.DataSourceType.GLOBAL_CACHE, type, uuid, null);
        pipelineDataSynchronizer.doSynchronisation(DataSynchronizer.DataSourceType.LOCAL, DataSynchronizer.DataSourceType.GLOBAL_STORAGE, type, uuid, null);
        vCoreData.save(false);
        plugin.consoleMessage("&aSaved &b" + uuid + " &8[&e" + type + "&8]", false);
        getLocalCache().remove(type, uuid);
    }

    @Override
    public void preloadAllData() {
        plugin.getServices().getSubsystemManager().getActiveServerDataClasses().forEach(this::preloadData);
        plugin.getServices().getSubsystemManager().getActivePlayerDataClasses().forEach(this::preloadData);
    }

    @Override
    public DataSynchronizer getSynchronizer() {
        return pipelineDataSynchronizer;
    }

    private <T extends VCoreData> T loadFromPipeline(@NotNull Class<? extends T> dataClass, @NotNull UUID uuid, boolean createIfNotExist) {
        // ExistCheck LocalCache
        if (localCache.dataExist(dataClass, uuid)) {
            plugin.consoleMessage("&eFound Data in Local Cache &8[&b" + dataClass.getSimpleName() + "&8]", 1, true);
        }
        // ExistCheck GlobalCache
        else if (globalCache != null && globalCache.dataExist(dataClass, uuid)) {
            plugin.consoleMessage("&eFound Data in Redis Cache &8[&b" + dataClass.getSimpleName() + "&8]", 1, true);
            pipelineDataSynchronizer.doSynchronisation(DataSynchronizer.DataSourceType.GLOBAL_CACHE, DataSynchronizer.DataSourceType.LOCAL, dataClass, uuid, null);
            //getRedisHandler().redisToLocal(dataClass, uuid);
        }
        // ExistCheck GlobalStorage
        else if (globalStorage != null && globalStorage.dataExist(dataClass, uuid)) {
            plugin.consoleMessage("&eFound Data in Database &8[&b" + dataClass.getSimpleName() + "&8]", 1, true);
            //getDatabaseHandler().databaseToLocal(dataClass,uuid);
            pipelineDataSynchronizer.doSynchronisation(DataSynchronizer.DataSourceType.GLOBAL_STORAGE, DataSynchronizer.DataSourceType.LOCAL, dataClass, uuid, null);

            if (AnnotationResolver.getDataProperties(dataClass).dataContext().equals(DataContext.GLOBAL))
                //globalStorage.dataBaseToRedis(dataClass, uuid);
                pipelineDataSynchronizer.doSynchronisation(DataSynchronizer.DataSourceType.GLOBAL_STORAGE, DataSynchronizer.DataSourceType.GLOBAL_CACHE, dataClass, uuid, null);
        } else {
            if (!createIfNotExist)
                return null;
            createNewData(dataClass, uuid);
        }
        plugin.consoleMessage("&eLoaded &a" + dataClass.getSimpleName() + " &ewith uuid&7: " + uuid, 1, true);
        if (!localCache.dataExist(dataClass, uuid))
            throw new NullPointerException("Error in dataPipeline while loading " + dataClass + " with uuid " + uuid);
        T data = localCache.getData(dataClass, uuid);
        data.updateLastUse();
        return data;
    }

    private <T extends VCoreData> T createNewData(@NotNull Class<? extends T> dataClass, @NotNull UUID uuid) {
        plugin.consoleMessage("&eNo Data was found. Creating new data! &8[&b" + dataClass.getSimpleName() + "&8]", 1, true);
        T vCoreData = localCache.instantiateData(dataClass, uuid);
        vCoreData.loadDependentData();
        vCoreData.onCreate();
        localCache.save(dataClass, vCoreData);

        pipelineDataSynchronizer.synchronize(DataSynchronizer.DataSourceType.LOCAL, DataSynchronizer.DataSourceType.GLOBAL_CACHE, dataClass, uuid);

        if (!NetworkData.class.isAssignableFrom(dataClass))
            pipelineDataSynchronizer.synchronize(DataSynchronizer.DataSourceType.LOCAL, DataSynchronizer.DataSourceType.GLOBAL_STORAGE, dataClass, uuid);
        return vCoreData;
    }

    private <S extends VCoreData> void preloadData(Class<? extends S> type) {
        VCoreDataProperties vCoreDataProperties = AnnotationResolver.getDataProperties(type);
        PreloadStrategy preloadStrategy = vCoreDataProperties.preloadStrategy();
        // Data will only be preloaded if it is declared properly
        if (!preloadStrategy.equals(PreloadStrategy.LOAD_BEFORE))
            return;
        plugin.consoleMessage("&ePreloading &b" + type.getSimpleName(), true);
        if (globalCache != null)
            globalCache.getSavedUUIDs(type).forEach(uuid -> pipelineDataSynchronizer.doSynchronisation(DataSynchronizer.DataSourceType.GLOBAL_CACHE, DataSynchronizer.DataSourceType.LOCAL, type, uuid, null));
        if (globalStorage != null)
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
        try {
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public VCorePlugin<?, ?> getPlugin() {
        return plugin;
    }
}
