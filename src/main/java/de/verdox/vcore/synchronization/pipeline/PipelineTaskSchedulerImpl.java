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
    private Map<UUID, PipelineTask<?>> tasks = new ConcurrentHashMap<>();

    public PipelineTaskSchedulerImpl(PipelineManager pipelineManager){
        this.pipelineManager = pipelineManager;
    }

    @Override
    public <T extends VCoreData> PipelineTask<T> schedulePipelineTask(@Nonnull PipelineAction pipelineAction, @Nonnull Pipeline.LoadingStrategy loadingStrategy, @Nonnull Class<? extends T> type, @Nonnull UUID uuid) {
            PipelineTask<T> existingTask = getExistingPipelineTask(type, uuid);
            if(existingTask != null){
                if(!Bukkit.isPrimaryThread()){
                    if(existingTask.getPipelineAction().equals(pipelineAction))
                        return existingTask;
                    else {
                        try { existingTask.getCompletableFuture().get(); } catch (InterruptedException | ExecutionException e) { e.printStackTrace(); }
                    }
                }
            }
        PipelineTask<T> pipelineTask = new PipelineTask<>(pipelineManager.getPlugin(), this, pipelineAction, type, uuid, () -> tasks.remove(uuid));
            if(!Bukkit.isPrimaryThread())
                pipelineManager.getPlugin().consoleMessage("&6Scheduling "+loadingStrategy+" PipelineTask &a"+type.getSimpleName()+" &7: "+pipelineTask.getObjectUUID(),true);
            else
                pipelineManager.getPlugin().consoleMessage("&6Scheduling "+loadingStrategy+" PipelineTask on Main Thread &a"+type.getSimpleName()+" &7: "+pipelineTask.getObjectUUID(),true);
        tasks.put(uuid, pipelineTask);
        return pipelineTask;
    }

    @Override
    public synchronized  <T extends VCoreData> PipelineTask<T> getExistingPipelineTask(@Nonnull Class<? extends T> type, @Nonnull UUID uuid) {
        if(!tasks.containsKey(uuid))
            return null;
        PipelineTask<?> task = tasks.get(uuid);
        if(!task.getType().equals(type))
            throw new IllegalStateException("Duplicate uuid in cache!");
        return (PipelineTask<T>) task;
    }

    @Override
    public void removePipelineTask(@Nonnull UUID uuid) {
        if(tasks.containsKey(uuid))
            tasks.remove(uuid).getCompletableFuture().cancel(true);
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public void shutdown() {
        tasks.forEach((uuid, pipelineTask) -> {
            try {
                pipelineTask.getCompletableFuture().get(1,TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        });
    }
}
