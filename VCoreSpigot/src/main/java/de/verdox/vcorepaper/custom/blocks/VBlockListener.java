package de.verdox.vcorepaper.custom.blocks;

import de.verdox.vcore.event.listener.VCoreListener;
import de.verdox.vcore.plugin.VCorePlugin;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldSaveEvent;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VBlockListener extends VCoreListener.VCoreBukkitListener {
    private final VBlockManager vBlockManager;
    private ExecutorService threadPool = Executors.newCachedThreadPool();

    public VBlockListener(VCorePlugin.Minecraft plugin, VBlockManager vBlockManager) {
        super(plugin);
        this.vBlockManager = vBlockManager;
    }

    @EventHandler
    public void chunkLoad(ChunkLoadEvent e){
        Chunk chunk = e.getChunk();
        if(this.vBlockManager.isCached(chunk))
            return;
        threadPool.submit(() -> {
            Set<Location> customBlockLocations = vBlockManager.getVBlockFileStorage().findCustomBlockLocations(chunk);
            customBlockLocations.parallelStream().forEach(location -> {
                BlockState blockState = location.getBlock().getState();
                vBlockManager.getOrCreateBlockPersistentData(blockState);
            });
        });
    }

    @EventHandler
    public void chunkUnload(ChunkUnloadEvent e){
        Chunk chunk = e.getChunk();
        if(!this.vBlockManager.isCached(chunk))
            return;

        threadPool.submit(() -> {
            vBlockManager.getDataOfChunk(chunk)
                    .parallelStream()
                    .filter(BlockPersistentData::readyToBeCleaned)
                    .forEach(blockPersistentData -> {
                        vBlockManager.removeAndSaveBlockPersistentData(blockPersistentData.getLocation().getBlock().getState());
                    });
        });
    }

    @EventHandler
    public void save(WorldSaveEvent e){

    }
}
