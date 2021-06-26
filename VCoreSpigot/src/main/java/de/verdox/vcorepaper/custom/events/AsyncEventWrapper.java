package de.verdox.vcorepaper.custom.events;

import de.verdox.vcore.plugin.listener.VCoreListener;
import de.verdox.vcore.plugin.VCorePlugin;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AsyncEventWrapper extends VCoreListener.VCoreBukkitListener {

    private final AsyncEventContainer<Block> asyncEventContainer = new AsyncEventContainer<>(this);
    private final Executor singleThreadExecutor = Executors.newSingleThreadExecutor();

    public AsyncEventWrapper(VCorePlugin.Minecraft plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e){
        Block block = e.getBlock();
        Player player = e.getPlayer();
        int expToDrop = e.getExpToDrop();
        boolean isDropItems = e.isDropItems();



        //TODO: 1. Block für dieses Event reservieren, wenn der Block verfügbar ist
        //asyncEventContainer.lock(e.getBlock());
        //e.setCancelled(true);



        //TODO: 2
    }


    public static class AsyncEventContainer<T>{

        private final AsyncEventWrapper asyncEventWrapper;
        private final Map<T, Lock> lockMap = new ConcurrentHashMap<>();

        AsyncEventContainer(AsyncEventWrapper asyncEventWrapper){
            this.asyncEventWrapper = asyncEventWrapper;
        }

        public void lock(T object){
            if(!lockMap.containsKey(object))
                lockMap.put(object,new ReentrantLock());
            ReentrantLock reentrantLock = (ReentrantLock) lockMap.get(object);
            reentrantLock.lock();
        }

        public void unlock(T object){
            if(!lockMap.containsKey(object))
                return;
            lockMap.get(object).unlock();
        }
    }
}
