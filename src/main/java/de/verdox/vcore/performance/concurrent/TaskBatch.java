/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.performance.concurrent;

import de.verdox.vcore.plugin.VCorePlugin;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.jetbrains.annotations.NotNull;
import reactor.util.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 20.06.2021 15:48
 */
public abstract class TaskBatch<V extends VCorePlugin<?, ?>> {

    private final List<TaskInfo> tasks = new ArrayList<>();
    private final V plugin;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(new DefaultThreadFactory("TaskBatch"));
    private final AtomicBoolean locked = new AtomicBoolean(false);
    private Runnable callback;

    public TaskBatch(@NotNull V plugin) {
        Objects.requireNonNull(plugin, "plugin can't be null");
        this.plugin = plugin;
    }

    public TaskBatch<V> doSync(@NotNull Runnable runnable) {
        Objects.requireNonNull(runnable, "Runnable can't be null!");
        addTask(TaskType.SYNC, runnable, 0);
        return this;
    }

    public TaskBatch<V> doAsync(@NotNull Runnable runnable) {
        Objects.requireNonNull(runnable, "Runnable can't be null!");
        addTask(TaskType.ASYNC, runnable, 0);
        return this;
    }

    public TaskBatch<V> wait(long delay, @NotNull TimeUnit timeUnit) {
        Objects.requireNonNull(timeUnit, "timeUnit can't be null!");
        addTask(TaskType.WAIT, null, timeUnit.toMillis(delay));
        return this;
    }

    public void executeBatch(@Nullable Runnable callback) {
        Objects.requireNonNull(callback, "Runnable can't be null!");
        this.callback = callback;
        executor.submit(new CatchingRunnable(this::runBatch));
    }

    public void executeBatch() {
        executeBatch(null);
    }

    public List<Runnable> interrupt() {
        return executor.shutdownNow();
    }

    private void runBatch() {
        for (int i = 0; i < tasks.size(); i++) {
            while (locked.get()) {
            }
            TaskInfo task = tasks.get(i);
            locked.set(true);
            if (task.taskType.equals(TaskType.SYNC))
                runSync(task.runnable);
            else if (task.taskType.equals(TaskType.ASYNC))
                runAsync(task.runnable);
            else {
                try {
                    Thread.sleep(task.delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                locked.set(false);
            }
        }
        if (callback != null)
            callback.run();
        onFinishBatch();
        executor.shutdown();
    }

    protected abstract void runSync(@NotNull Runnable runnable);

    protected abstract void runAsync(@NotNull Runnable runnable);

    protected abstract void onFinishBatch();

    protected V getPlugin() {
        return plugin;
    }

    private void addTask(@NotNull TaskType taskType, @Nullable Runnable runnable, long delay) {
        Objects.requireNonNull(taskType, "taskType can't be null!");
        tasks.add(new TaskInfo(delay, taskType, () -> {
            if (runnable != null)
                runnable.run();
            locked.set(false);
        }));
    }

    enum TaskType {
        SYNC,
        ASYNC,
        WAIT
    }

    static class TaskInfo {
        private final TaskType taskType;
        private final Runnable runnable;
        private final long delay;

        public TaskInfo(long delay, TaskType taskType, Runnable runnable) {
            this.delay = delay;
            this.taskType = taskType;
            this.runnable = runnable;
        }
    }
}
