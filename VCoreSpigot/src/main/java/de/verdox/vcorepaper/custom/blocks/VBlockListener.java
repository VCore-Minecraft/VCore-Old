package de.verdox.vcorepaper.custom.blocks;

import de.verdox.vcore.event.listener.VCoreListener;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcorepaper.VCorePaper;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldSaveEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VBlockListener extends VCoreListener.VCoreBukkitListener {
    private final VBlockManager vBlockManager;
    private ExecutorService threadPool = Executors.newSingleThreadExecutor();

    public VBlockListener(VCorePlugin.Minecraft plugin, VBlockManager vBlockManager) {
        super(plugin);
        this.vBlockManager = vBlockManager;
    }

    @EventHandler
    public void chunkLoad(ChunkLoadEvent e){
        if(e.isNewChunk())
            return;
        Chunk chunk = e.getChunk();
        if(vBlockManager.isCached(chunk))
            return;
        threadPool.submit(() -> {
            //long time = System.currentTimeMillis();
            vBlockManager
                    .getVBlockFileStorage()
                    .findCustomBlockLocations(chunk)
                    .forEach(location -> {
                    });
            //VCorePaper.getInstance().consoleMessage("&eChunkData loaded in&7: "+(System.currentTimeMillis() - time)+"ms",true);
        });
    }

    @EventHandler
    public void chunkUnloadEvent(ChunkUnloadEvent e){
        if(!e.isSaveChunk())
            return;
        Chunk chunk = e.getChunk();
        if(!vBlockManager.isCached(chunk))
            return;
        threadPool.submit(() -> {
            //long time = System.currentTimeMillis();
            vBlockManager.getDataOfChunk(chunk)
                    .stream()
                    .filter(BlockPersistentData::readyToBeCleaned)
                    .forEach(blockPersistentData -> {
                       vBlockManager.removeAndSaveBlockPersistentData(blockPersistentData.getLocation().getBlock().getState());
                     });
            //VCorePaper.getInstance().consoleMessage("&eChunkData unloaded in&7: "+(System.currentTimeMillis() - time)+"ms",true);
        });
    }

    @EventHandler
    public void save(WorldSaveEvent e){

    }
}
