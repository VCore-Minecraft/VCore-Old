package de.verdox.vcore.plugin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VCoreScheduler {
    private final VCorePlugin<?, ?> vCorePlugin;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);

    VCoreScheduler(VCorePlugin<?,?> vCorePlugin){
        this.vCorePlugin = vCorePlugin;
    }

    public void asyncInterval(Runnable task, long delay, long interval){
        scheduledExecutorService.scheduleAtFixedRate(task,delay*50,interval*50, TimeUnit.MILLISECONDS);
    }

    public void asyncSchedule(Runnable task, long delay, long interval){
        scheduledExecutorService.scheduleAtFixedRate(task,delay*50,interval*50, TimeUnit.MILLISECONDS);
    }

    public void async(Runnable task){
        scheduledExecutorService.execute(task);
    }

    public void waitUntilShutdown(){
        shutDown();
        vCorePlugin.consoleMessage("&6Awaiting Scheduler to shut down&7!",true);
        try { scheduledExecutorService.awaitTermination(30,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            vCorePlugin.consoleMessage("&cScheduler was interrupted&7!",true);
        }
    }

    public void shutDown(){
        vCorePlugin.consoleMessage("&6Shutting down Scheduler&7!",true);
        scheduledExecutorService.shutdown();
    }
}
