/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline;

import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import de.verdox.vcore.synchronization.pipeline.parts.storage.PipelineTaskScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 28.06.2021 20:10
 */
public class PipelineTaskSchedulerImpl implements PipelineTaskScheduler {

    private final PipelineManager pipelineManager;
    private final Map<UUID, Map<Class<? extends VCoreData>, PipelineTask<?>>> pendingTasks = new ConcurrentHashMap<>();

    public PipelineTaskSchedulerImpl(@NotNull PipelineManager pipelineManager) {
        Objects.requireNonNull(pipelineManager, "pipelineManager can't be null!");
        this.pipelineManager = pipelineManager;
    }

    @Override
    public synchronized <T extends VCoreData> PipelineTask<T> schedulePipelineTask(@NotNull PipelineAction pipelineAction, @NotNull Pipeline.LoadingStrategy loadingStrategy, @NotNull Class<? extends T> type, @NotNull(exception = IllegalArgumentException.class) UUID uuid) {
        Objects.requireNonNull(type, "type can't be null!");
        Objects.requireNonNull(uuid, "uuid can't be null!");
        PipelineTask<T> existingTask = getExistingPipelineTask(type, uuid);
        if (existingTask != null) {
            //pipelineManager.getPlugin().consoleMessage("&8[&e" + loadingStrategy + "&8] &eFound existing Pipeline Task: " + existingTask, true);
            return existingTask;
        }
        PipelineTask<T> pipelineTask = new PipelineTask<>(pipelineManager.getPlugin(), this, pipelineAction, type, uuid, () -> removePipelineTask(type, uuid));
        //pipelineManager.getPlugin().consoleMessage("&8[&e" + loadingStrategy + "&8] &eScheduling Pipeline Task: " + pipelineTask, true);

        if (!pendingTasks.containsKey(uuid))
            pendingTasks.put(uuid, new ConcurrentHashMap<>());
        pendingTasks.get(uuid).put(type, pipelineTask);
        return pipelineTask;
    }

    @Override
    public synchronized <T extends VCoreData> PipelineTask<T> getExistingPipelineTask(@NotNull Class<? extends T> type, @NotNull UUID uuid) {
        Objects.requireNonNull(type, "type can't be null!");
        Objects.requireNonNull(uuid, "uuid can't be null!");
        if (!pendingTasks.containsKey(uuid))
            return null;
        Map<Class<? extends VCoreData>, PipelineTask<?>> map = pendingTasks.get(uuid);
        if (!map.containsKey(type))
            return null;
        PipelineTask<?> task = map.get(type);
        return (PipelineTask<T>) task;
    }

    @Override
    public synchronized <T extends VCoreData> void removePipelineTask(@NotNull Class<? extends T> type, @NotNull UUID uuid) {
        Objects.requireNonNull(type, "type can't be null!");
        Objects.requireNonNull(uuid, "uuid can't be null!");
        if (!pendingTasks.containsKey(uuid))
            return;
        pendingTasks.get(uuid).remove(type);
        if (pendingTasks.get(uuid).isEmpty())
            pendingTasks.remove(uuid);
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public void shutdown() {
        pipelineManager.getPlugin().consoleMessage("&eShutting down Pipeline Task Scheduler", false);
        pendingTasks.forEach((uuid, pipelineTasks) -> {
            pipelineTasks.forEach((aClass, pipelineTask) -> {
                try {
                    pipelineTask.getCompletableFuture().get(1, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    pipelineManager.getPlugin().consoleMessage("&cPipeline Task took too long for type: &b" + Arrays.toString(pipelineTask.getCompletableFuture().getClass().getGenericInterfaces()), false);
                    e.printStackTrace();
                }
            });
        });
        pipelineManager.getPlugin().consoleMessage("&aPipeline Task Scheduler shut down successfully", false);
    }
}
