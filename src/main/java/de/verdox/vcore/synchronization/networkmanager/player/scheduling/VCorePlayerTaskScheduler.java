/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.scheduling;

import de.verdox.vcore.performance.concurrent.CatchingRunnable;
import de.verdox.vcore.plugin.SystemLoadable;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.networkmanager.player.api.PlayerTask;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.08.2021 15:07
 */
public final class VCorePlayerTaskScheduler implements SystemLoadable {
    protected final Map<UUID, Set<PlayerTask>> scheduledTasks = new ConcurrentHashMap<>();
    private final VCorePlugin<?, ?> plugin;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public VCorePlayerTaskScheduler(VCorePlugin<?, ?> plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public void shutdown() {
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void schedulePlayerTask(@Nonnull UUID playerUUID, @Nonnull Runnable runnable, int maxWaitTime, @Nonnull TimeUnit timeUnit) {
        schedulePlayerTask(playerUUID, UUID.randomUUID(), runnable, maxWaitTime, timeUnit);
    }

    public void schedulePlayerTask(@Nonnull UUID playerUUID, @NotNull UUID taskUUID, @Nonnull Runnable runnable, int maxWaitTime, @Nonnull TimeUnit timeUnit) {
        plugin.consoleMessage("&eScheduling Task", false);
        executor.submit(new CatchingRunnable(() -> {
            if (plugin.getPlatformWrapper().isPlayerOnline(playerUUID)) {
                plugin.consoleMessage("&eExecuting Task", false);
                plugin.sync(runnable);
                return;
            }
            PlayerTask playerTask = new PlayerTask(playerUUID, taskUUID, runnable);
            if (!scheduledTasks.containsKey(playerUUID))
                scheduledTasks.put(playerUUID, new HashSet<>());
            scheduledTasks.get(playerUUID).add(playerTask);
            plugin.getServices().getVCoreScheduler().asyncSchedule(() -> {
                executor.submit(() -> {
                    removePlayerTask(playerUUID, playerTask);
                });
            }, maxWaitTime, timeUnit);
        }));
    }

    private void removePlayerTask(@Nonnull UUID playerUUID, @Nonnull PlayerTask playerTask) {
        if (!scheduledTasks.containsKey(playerUUID))
            return;
        Set<PlayerTask> playerTasks = scheduledTasks.get(playerUUID);
        playerTasks.remove(playerTask);
        if (playerTasks.isEmpty())
            scheduledTasks.remove(playerUUID);
    }

    public CompletableFuture<Set<Runnable>> getAllTasks(@Nonnull UUID playerUUID) {
        CompletableFuture<Set<Runnable>> future = new CompletableFuture<>();
        executor.submit(new CatchingRunnable(() -> {
            if (!scheduledTasks.containsKey(playerUUID)) {
                future.complete(new HashSet<>());
                return;
            }
            future.complete(scheduledTasks.get(playerUUID).stream().map(PlayerTask::getRunnable).collect(Collectors.toSet()));
        }));
        return future;
    }
}
