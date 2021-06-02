package de.verdox.vcorepaper.custom.blocks;

import de.verdox.vcore.event.listener.VCoreListener;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcorepaper.VCorePaper;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldSaveEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VBlockListener extends VCoreListener.VCoreBukkitListener {
    private final VBlockManager vBlockManager;
    private final ExecutorService threadPool = Executors.newSingleThreadExecutor();

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
        Bukkit.getScheduler().runTaskAsynchronously(plugin,() -> {
            vBlockManager
                    .getVBlockFileStorage()
                    .findCustomBlockLocations(chunk)
                    .forEach(location -> vBlockManager.getOrLoadBlockPersistentData(location.getBlock().getState()));
        });
        //threadPool.submit(() -> {
            //long time = System.currentTimeMillis();

            //VCorePaper.getInstance().consoleMessage("&eChunkData loaded in&7: "+(System.currentTimeMillis() - time)+"ms",true);
        //});
    }

    @EventHandler
    public void chunkUnloadEvent(ChunkUnloadEvent e){
        Chunk chunk = e.getChunk();
        if(!vBlockManager.isCached(chunk))
            return;
        Bukkit.getScheduler().runTaskAsynchronously(plugin,() -> {
            //long time = System.currentTimeMillis();
            vBlockManager.getDataOfChunk(chunk)
                    //.filter(BlockPersistentData::readyToBeCleaned)
                    .forEach(blockPersistentData -> {
                        if(e.isSaveChunk())
                            vBlockManager.removeAndSaveBlockPersistentData(blockPersistentData.getLocation().getBlock().getState());
                        else
                            vBlockManager.removeBlockPersistentData(blockPersistentData.getLocation().getBlock().getState());
                    });
            //VCorePaper.getInstance().consoleMessage("&eChunkData unloaded in&7: "+(System.currentTimeMillis() - time)+"ms",true);
        });
        threadPool.submit(() -> {

        });
    }

    @EventHandler
    public void blockFromTo(BlockFromToEvent e){
        Block block = e.getToBlock();
        VBlock vBlock = VCorePaper.getInstance().getVBlockManager().getCachedVBlock(block.getState());
        if(vBlock == null)
            return;
        e.setCancelled(!vBlock.isBlockPermissionAllowed(VBlockEventPermission.BLOCK_LIQUID_EVENT));
    }

    @EventHandler
    public void blockGrowEvent(BlockGrowEvent e){
        Block block = e.getBlock();
        VBlock vBlock = VCorePaper.getInstance().getVBlockManager().getCachedVBlock(block.getState());
        if(vBlock == null)
            return;
        e.setCancelled(!vBlock.isBlockPermissionAllowed(VBlockEventPermission.BLOCK_GROW_EVENT));
    }

    @EventHandler
    public void blockPhysicsEvent(BlockPhysicsEvent e){
        Block block = e.getBlock();
        VBlock vBlock = VCorePaper.getInstance().getVBlockManager().getCachedVBlock(block.getState());
        if(vBlock == null)
            return;
        e.setCancelled(!vBlock.isBlockPermissionAllowed(VBlockEventPermission.BLOCK_GRAVITY_EVENT));
    }

    @EventHandler
    public void explodeEvent(BlockExplodeEvent e){
        Block block = e.getBlock();
        VBlock vBlock = VCorePaper.getInstance().getVBlockManager().getCachedVBlock(block.getState());
        if(vBlock == null)
            return;
        e.setCancelled(!vBlock.isBlockPermissionAllowed(VBlockEventPermission.BLOCK_EXPLODE_EVENT));
    }

    @EventHandler
    public void dropItems(BlockDropItemEvent e){
        Block block = e.getBlock();
        VBlock vBlock = VCorePaper.getInstance().getVBlockManager().getCachedVBlock(block.getState());
        if(vBlock == null)
            return;
        e.setCancelled(!vBlock.isBlockPermissionAllowed(VBlockEventPermission.BLOCK_DROP_ITEMS_EVENT));
    }

    @EventHandler
    public void blockPistonExtendEvent(BlockPistonExtendEvent e){
        for (Block block : e.getBlocks()) {
            VBlock vBlock = VCorePaper.getInstance().getVBlockManager().getCachedVBlock(block.getState());
            if(vBlock == null)
                return;
            e.setCancelled(!vBlock.isBlockPermissionAllowed(VBlockEventPermission.BLOCK_PISTON_EVENT));
            return;
        }
    }

    @EventHandler
    public void blockPistonRetractEvent(BlockPistonRetractEvent e){
        for (Block block : e.getBlocks()) {
            VBlock vBlock = VCorePaper.getInstance().getVBlockManager().getCachedVBlock(block.getState());
            if(vBlock == null)
                return;
            e.setCancelled(!vBlock.isBlockPermissionAllowed(VBlockEventPermission.BLOCK_PISTON_EVENT));
            return;
        }
    }

    @EventHandler
    public void blockBurnEvent(BlockBurnEvent e){
        Block block = e.getBlock();
        VBlock vBlock = VCorePaper.getInstance().getVBlockManager().getCachedVBlock(block.getState());
        if(vBlock == null)
            return;
        e.setCancelled(!vBlock.isBlockPermissionAllowed(VBlockEventPermission.BLOCK_DROP_ITEMS_EVENT));
    }

    @EventHandler
    public void save(WorldSaveEvent e){
        Bukkit.getScheduler().runTaskAsynchronously(plugin,() -> {
            //long time = System.currentTimeMillis();
            for (Chunk chunk : e.getWorld().getLoadedChunks()) {
                vBlockManager.getDataOfChunk(chunk)
                        //.filter(BlockPersistentData::readyToBeCleaned)
                        .forEach(blockPersistentData -> {
                            vBlockManager.saveBlockPersistentData(blockPersistentData.getLocation().getBlock().getState());
                        });
                //VCorePaper.getInstance().consoleMessage("&eChunkData unloaded in&7: "+(System.currentTimeMillis() - time)+"ms",true);
            }
        });
    }
}
