/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.parts.storage;

import de.verdox.vcore.plugin.SystemLoadable;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 28.06.2021 20:05
 */
public interface PipelineTaskScheduler extends SystemLoadable {

    <T extends VCoreData> PipelineTask<T> schedulePipelineTask(@Nonnull PipelineAction pipelineAction, @Nonnull Pipeline.LoadingStrategy loadingStrategy, @Nonnull Class<? extends T> type, @Nonnull UUID uuid);
    <T extends VCoreData> PipelineTask<T> getExistingPipelineTask(@Nonnull Class<? extends T> type, @Nonnull UUID uuid);
    void removePipelineTask(@Nonnull UUID uuid);

    class PipelineTask<T extends VCoreData>{
        private final PipelineAction pipelineAction;
        private final Class<? extends VCoreData> type;
        private final UUID uuid;
        private final CompletableFuture<T> completableFuture;
        private final UUID taskUUID = UUID.randomUUID();
        private long start = System.currentTimeMillis();

        public PipelineTask(VCorePlugin<?,?> plugin, PipelineTaskScheduler pipelineTaskScheduler, PipelineAction pipelineAction, Class<? extends T> type, UUID uuid, Runnable onComplete){
            this.pipelineAction = pipelineAction;
            this.type = type;
            this.uuid = uuid;
            this.completableFuture = new CompletableFuture<>();
            this.completableFuture.whenComplete((t, throwable) -> {
                plugin.consoleMessage("&6Task &a"+type.getSimpleName()+" &6done&7: "+getObjectUUID()+" &8[&e"+(System.currentTimeMillis() - start)+"ms&8]", true);
                onComplete.run();
                pipelineTaskScheduler.removePipelineTask(uuid);
            });
        }

        public Class<? extends VCoreData> getType() {
            return type;
        }

        public UUID getObjectUUID() {
            return uuid;
        }

        public PipelineAction getPipelineAction() {
            return pipelineAction;
        }

        public CompletableFuture<T> getCompletableFuture() {
            return completableFuture;
        }

        public UUID getTaskUUID() {
            return taskUUID;
        }
    }

    enum PipelineAction{
        LOAD
    }
}
