/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.concurrent;

import de.verdox.vcore.plugin.VCorePlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 20.06.2021 15:48
 */
public abstract class TaskBatch<V extends VCorePlugin<?,?>> {

    private final List<TaskInfo> tasks = new ArrayList<>();
    private final V plugin;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final AtomicBoolean locked = new AtomicBoolean(false);

    public TaskBatch(V plugin){
        this.plugin = plugin;
    }

    public TaskBatch<V> doSync(CatchingRunnable runnable){
        addTask(TaskType.SYNC,runnable);
        return this;
    }

    public TaskBatch<V> doAsync(CatchingRunnable runnable){
        addTask(TaskType.ASYNC,runnable);
        return this;
    }

    public void executeBatch(){
        executor.submit(new CatchingRunnable(this::runBatch));
    }

    private void runBatch(){
        for (int i = 0; i < tasks.size(); i++) {
            while(locked.get()){}
            TaskInfo task = tasks.get(i);
            System.out.println("Starting: "+i);
            locked.set(true);
            if(task.taskType.equals(TaskType.SYNC))
                runSync(task.runnable);
            else
                runAsync(task.runnable);
        }
        onFinishBatch();
    }

    protected abstract void runSync(CatchingRunnable runnable);
    protected abstract void runAsync(CatchingRunnable runnable);
    protected abstract void onFinishBatch();

    protected V getPlugin() {
        return plugin;
    }

    private void addTask(TaskType taskType, CatchingRunnable runnable){
        tasks.add(new TaskInfo(taskType, new CatchingRunnable(() -> {
            runnable.run();
            System.out.println("unlocking now");
            locked.set(false);
        })));
    }

    static class TaskInfo{
        private final TaskType taskType;
        private final CatchingRunnable runnable;

        public TaskInfo(TaskType taskType, CatchingRunnable runnable){
            this.taskType = taskType;
            this.runnable = runnable;
        }
    }

    enum TaskType{
        SYNC,
        ASYNC
    }
}
