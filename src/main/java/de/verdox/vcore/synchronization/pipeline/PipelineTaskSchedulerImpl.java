/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline;

import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import de.verdox.vcore.synchronization.pipeline.parts.storage.PipelineTaskScheduler;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 28.06.2021 20:10
 */
public class PipelineTaskSchedulerImpl implements PipelineTaskScheduler {

    private final PipelineManager pipelineManager;
    private final Map<UUID, Map<Class<? extends VCoreData>, PipelineTask<?>>> pendingTasks = new ConcurrentHashMap<>();

    public PipelineTaskSchedulerImpl(PipelineManager pipelineManager){
        this.pipelineManager = pipelineManager;
    }

    @Override
    public <T extends VCoreData> PipelineTask<T> schedulePipelineTask(@Nonnull PipelineAction pipelineAction, @Nonnull Pipeline.LoadingStrategy loadingStrategy, @Nonnull Class<? extends T> type, @Nonnull UUID uuid) {
        PipelineTask<T> existingTask = getExistingPipelineTask(type, uuid);
        if(existingTask != null) {
            pipelineManager.getPlugin().consoleMessage("&8[&e"+loadingStrategy+"&8] &eFound existing Pipeline Task: "+existingTask,true);
            return existingTask;
        }
        PipelineTask<T> pipelineTask = new PipelineTask<>(pipelineManager.getPlugin(), this, pipelineAction, type, uuid, () -> removePipelineTask(type,uuid));
        pipelineManager.getPlugin().consoleMessage("&8[&e"+loadingStrategy+"&8] &eScheduling Pipeline Task: "+pipelineTask,true);

        if(!pendingTasks.containsKey(uuid))
            pendingTasks.put(uuid, new ConcurrentHashMap<>());
        pendingTasks.get(uuid).put(type, pipelineTask);
        return pipelineTask;
    }

    @Override
    public synchronized  <T extends VCoreData> PipelineTask<T> getExistingPipelineTask(@Nonnull Class<? extends T> type, @Nonnull UUID uuid) {
        if(!pendingTasks.containsKey(uuid))
            return null;
        Map<Class<? extends VCoreData>, PipelineTask<?>> map = pendingTasks.get(uuid);
        if(!map.containsKey(type))
            return null;
        PipelineTask<?> task = map.get(type);
        return (PipelineTask<T>) task;
    }

    @Override
    public <T extends VCoreData> void removePipelineTask(@Nonnull Class<? extends T> type, @Nonnull UUID uuid) {
        if(!pendingTasks.containsKey(uuid))
            return;
        pendingTasks.get(uuid).remove(type);
        if(pendingTasks.get(uuid).isEmpty())
            pendingTasks.remove(uuid);
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public void shutdown() {
        pipelineManager.getPlugin().consoleMessage("&eShutting down Pipeline Task Scheduler",false);
        pendingTasks.forEach((uuid, pipelineTasks) -> {
            pipelineTasks.forEach((aClass, pipelineTask) -> {
                try {
                    pipelineTask.getCompletableFuture().get(1,TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                }
            });
        });
        pipelineManager.getPlugin().consoleMessage("&aPipeline Task Scheduler shut down successfully",false);
    }
}
