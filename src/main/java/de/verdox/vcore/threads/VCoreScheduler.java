package de.verdox.vcore.threads;

import de.verdox.vcore.plugin.VCorePlugin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VCoreScheduler {
    private VCorePlugin<?, ?> vCorePlugin;
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);

    public VCoreScheduler(VCorePlugin<?,?> vCorePlugin){
        this.vCorePlugin = vCorePlugin;
    }

    public void runTaskInterval(Runnable task, long delay, long interval){
        scheduledExecutorService.scheduleAtFixedRate(task,delay*50,interval*50, TimeUnit.MILLISECONDS);
    }

    public void runTask(Runnable task){
        scheduledExecutorService.execute(task);
    }

    public void shutDown(){
        scheduledExecutorService.shutdown();
    }
}
