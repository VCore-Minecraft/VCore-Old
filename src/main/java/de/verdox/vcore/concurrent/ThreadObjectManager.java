package de.verdox.vcore.concurrent;

import de.verdox.vcore.util.keys.VCoreKey;

import java.util.Map;
import java.util.concurrent.*;

public class ThreadObjectManager<T extends VCoreKey> {

    private final Map<T, ThreadObject<T>> cache;
    private final ScheduledExecutorService clearingService;

    public ThreadObjectManager(){
        this.cache = new ConcurrentHashMap<>();
        this.clearingService = Executors.newSingleThreadScheduledExecutor();
        clearingService.scheduleAtFixedRate(() -> {
            synchronized (cache){
                    cache
                            .keySet()
                            .forEach(objectKey -> {
                                ThreadObject<T> threadObject = cache.get(objectKey);
                                if(!threadObject.isOld())
                                    return;
                                threadObject.shutdown();
                                cache.remove(objectKey);
                        });
            }
        },0L,300, TimeUnit.SECONDS);
    }

    public void submitTask(T object, Runnable runnable){
        if(!cache.containsKey(object))
            cache.put(object, new ThreadObject<>(Executors.newSingleThreadScheduledExecutor()));
        cache.get(object).submit(runnable);
    }

    public void shutdown(){
        clearingService.shutdownNow();
        cache.forEach((t, executorService) -> executorService.shutdown());
        cache.clear();
    }

    public static class ThreadObject<T>{
        private static final long TIME_UNTIL_OLD = 1000L*300;
        private long lastUse = System.currentTimeMillis();
        private final ExecutorService executorService;

        ThreadObject(ExecutorService executorService){
            this.executorService = executorService;
        }

        void updateUse(){
            lastUse = System.currentTimeMillis();
        }

        void submit(Runnable runnable){
            updateUse();
            executorService.submit(runnable);
        }

        void shutdown(){
            executorService.shutdown();
        }

        boolean isOld(){
            return (System.currentTimeMillis() - lastUse) >= TIME_UNTIL_OLD;
        }
    }
}
