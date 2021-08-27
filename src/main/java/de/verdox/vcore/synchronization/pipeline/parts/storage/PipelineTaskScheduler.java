/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.parts.storage;

import de.verdox.vcore.plugin.SystemLoadable;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 28.06.2021 20:05
 */
public interface PipelineTaskScheduler extends SystemLoadable {

    <T extends VCoreData> PipelineTask<T> schedulePipelineTask(@NotNull PipelineAction pipelineAction, @NotNull Pipeline.LoadingStrategy loadingStrategy, @NotNull Class<? extends T> type, @NotNull UUID uuid);

    <T extends VCoreData> PipelineTask<T> getExistingPipelineTask(@NotNull Class<? extends T> type, @NotNull UUID uuid);

    <T extends VCoreData> void removePipelineTask(@NotNull Class<? extends T> type, @NotNull UUID uuid);

    enum PipelineAction {
        LOAD
    }

    class PipelineTask<T extends VCoreData> {
        private final PipelineAction pipelineAction;
        private final Class<? extends VCoreData> type;
        private final UUID uuid;
        private final CompletableFuture<T> completableFuture;
        private final UUID taskUUID = UUID.randomUUID();
        private final long start = System.currentTimeMillis();

        public PipelineTask(VCorePlugin<?, ?> plugin, PipelineTaskScheduler pipelineTaskScheduler, PipelineAction pipelineAction, Class<? extends T> type, UUID uuid, Runnable onComplete) {
            this.pipelineAction = pipelineAction;
            this.type = type;
            this.uuid = uuid;
            this.completableFuture = new CompletableFuture<>();
            this.completableFuture.whenComplete((t, throwable) -> {
                //plugin.consoleMessage("&6Task &a" + taskUUID + " &6done&7: " + type + "  |  " + getObjectUUID() + " &8[&e" + t + "&8] &8[&e" + (System.currentTimeMillis() - start) + "ms&8]", true);
                onComplete.run();
                pipelineTaskScheduler.removePipelineTask(type, uuid);
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
}
