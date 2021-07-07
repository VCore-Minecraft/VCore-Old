/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline;

import de.verdox.vcore.plugin.SystemLoadable;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 06.07.2021 01:17
 */
public class PipelineQueue {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Map<UUID,PipelineTask<?>> tasks = new ConcurrentHashMap<>();
    private boolean running = true;

    public void test(){

    }

    public abstract static class PipelineTask<T>{
        protected final VCorePlugin<?, ?> plugin;
        protected final PipelineQueue pipelineQueue;
        private final CompletableFuture<T> completableFuture = new CompletableFuture<>();

        PipelineTask(VCorePlugin<?,?> plugin, PipelineQueue pipelineQueue, UUID objectUUID, Class<? extends VCoreData> dataClass){
            this.plugin = plugin;
            this.pipelineQueue = pipelineQueue;
        }

        public final CompletableFuture<T> getResult(){
            return completableFuture;
        }
    }

    public static class GetTask <T extends VCoreData> extends PipelineTask<T>{
        GetTask(VCorePlugin<?, ?> plugin, PipelineQueue pipelineQueue, UUID objectUUID, Class<? extends VCoreData> dataClass) {
            super(plugin, pipelineQueue, objectUUID, dataClass);
        }
    }
}
