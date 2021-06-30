/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.performance.concurrent;

import de.verdox.vcore.plugin.SystemLoadable;
import de.verdox.vcore.plugin.VCorePlugin;
import io.netty.util.concurrent.DefaultThreadFactory;

import javax.annotation.Nonnull;
import java.util.concurrent.*;

public class VCoreScheduler implements SystemLoadable {
    private final VCorePlugin<?, ?> vCorePlugin;
    private final ScheduledExecutorService scheduledExecutorService;

    public VCoreScheduler(VCorePlugin<?, ?> vCorePlugin){
        this.vCorePlugin = vCorePlugin;
        this.scheduledExecutorService  = Executors.newScheduledThreadPool(2,new DefaultThreadFactory(vCorePlugin.getPluginName()+"Scheduler"));
    }

    public void asyncInterval(Runnable task, long delay, long interval){
        vCorePlugin.consoleMessage("&7Scheduling Async Interval Task",true);
        scheduledExecutorService.scheduleAtFixedRate(task,delay*50,interval*50, TimeUnit.MILLISECONDS);
    }

    public void asyncSchedule(Runnable task, long delay, long interval){
        vCorePlugin.consoleMessage("&7Scheduling Async task for later",true);
        scheduledExecutorService.scheduleAtFixedRate(task,delay*50,interval*50, TimeUnit.MILLISECONDS);
    }

    public void async(Runnable task){
        vCorePlugin.consoleMessage("&7Scheduling Async task",true);
        scheduledExecutorService.execute(task);
    }

    public void waitUntilShutdown(){
        shutdown();
        vCorePlugin.consoleMessage("&6Awaiting Scheduler to shut down&7!",true);
        try { scheduledExecutorService.awaitTermination(30,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            vCorePlugin.consoleMessage("&cScheduler was interrupted&7!",true);
        }
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public void shutdown() {
        vCorePlugin.consoleMessage("&6Shutting down Scheduler&7!",true);
        scheduledExecutorService.shutdown();
    }
}
