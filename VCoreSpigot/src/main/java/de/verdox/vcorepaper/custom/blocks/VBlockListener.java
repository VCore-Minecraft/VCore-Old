package de.verdox.vcorepaper.custom.blocks;

import de.verdox.vcore.plugin.listener.VCoreListener;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.blocks.enums.VBlockEventPermission;
import de.verdox.vcorepaper.custom.blocks.files.VBlockSaveFile;
import io.netty.util.concurrent.DefaultThreadFactory;
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
    private final CustomBlockManager vBlockManager;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(4, new DefaultThreadFactory("VBlockChunk-Thread"));

    public VBlockListener(VCorePlugin.Minecraft plugin, CustomBlockManager vBlockManager) {
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
            vBlockManager
                    .getVBlockStorage()
                    .findCustomBlockLocations(chunk)
                    .forEach(vBlockManager::loadSaveFile);
        });
    }

    @EventHandler
    public void chunkUnloadEvent(ChunkUnloadEvent e){
        Chunk chunk = e.getChunk();
        if(!vBlockManager.isCached(chunk))
            return;
        threadPool.submit(() -> {
            vBlockManager.getDataOfChunk(chunk)
                    .forEach(vBlockSaveFile -> {
                        if(e.isSaveChunk())
                            vBlockSaveFile.save();
                        vBlockManager.unloadSaveFile(vBlockSaveFile);
                    });
        });
    }

    @EventHandler
    public void blockFromTo(BlockFromToEvent e){
        Block block = e.getToBlock();
        VBlock vBlock = VCorePaper.getInstance().getCustomBlockManager().getVBlock(block.getLocation());
        if(vBlock == null)
            return;
        vBlock.updateBlockData(block.getBlockData());
        e.setCancelled(!vBlock.isBlockPermissionAllowed(VBlockEventPermission.BLOCK_LIQUID_EVENT));
    }

    @EventHandler
    public void blockGrowEvent(BlockGrowEvent e){
        Block block = e.getBlock();
        VBlock vBlock = VCorePaper.getInstance().getCustomBlockManager().getVBlock(block.getLocation());

        if(vBlock != null){
            vBlock.updateBlockData(block.getBlockData());
            e.setCancelled(!vBlock.isBlockPermissionAllowed(VBlockEventPermission.BLOCK_GROW_EVENT));
            return;
        }
        //Block stemBlock = VCoreUtil.getBukkitWorldUtil().findStem(block);
        //TODO: Für Crops fertig machen
    }

    @EventHandler
    public void blockPhysicsEvent(BlockPhysicsEvent e){
        Block block = e.getBlock();
        Block sourceBlock = e.getSourceBlock();
        VBlock vBlock = VCorePaper.getInstance().getCustomBlockManager().getVBlock(block.getLocation());
        VBlock sourceVBlock = VCorePaper.getInstance().getCustomBlockManager().getVBlock(sourceBlock.getLocation());

        if(vBlock != null) {
            vBlock.updateBlockData(block.getBlockData());
            e.setCancelled(!vBlock.isBlockPermissionAllowed(VBlockEventPermission.BLOCK_GRAVITY_EVENT));
        }
        else if(sourceVBlock != null){
            sourceVBlock.updateBlockData(sourceBlock.getBlockData());
            e.setCancelled(!sourceVBlock.isBlockPermissionAllowed(VBlockEventPermission.BLOCK_GRAVITY_EVENT));
        }
    }

    @EventHandler
    public void leavesDecayEvent(LeavesDecayEvent e){
        Block block = e.getBlock();
        VBlock vBlock = VCorePaper.getInstance().getCustomBlockManager().getVBlock(block.getLocation());
        if(vBlock == null)
            return;
        vBlock.updateBlockData(block.getBlockData());
        e.setCancelled(!vBlock.isBlockPermissionAllowed(VBlockEventPermission.BLOCK_LEAVES_DECAY_EVENT));
    }

    @EventHandler
    public void explodeEvent(BlockExplodeEvent e){
        Block block = e.getBlock();
        VBlock vBlock = VCorePaper.getInstance().getCustomBlockManager().getVBlock(block.getLocation());
        if(vBlock == null)
            return;
        vBlock.updateBlockData(block.getBlockData());
        e.setCancelled(!vBlock.isBlockPermissionAllowed(VBlockEventPermission.BLOCK_EXPLODE_EVENT));
    }

    @EventHandler
    public void dropItems(BlockDropItemEvent e){
        Block block = e.getBlock();
        VBlock vBlock = VCorePaper.getInstance().getCustomBlockManager().getVBlock(block.getLocation());
        if(vBlock == null)
            return;
        vBlock.updateBlockData(block.getBlockData());
        e.setCancelled(!vBlock.isBlockPermissionAllowed(VBlockEventPermission.BLOCK_DROP_ITEMS_EVENT));
    }

    @EventHandler
    public void blockPistonExtendEvent(BlockPistonExtendEvent e){
        for (Block block : e.getBlocks()) {
            VBlock vBlock = VCorePaper.getInstance().getCustomBlockManager().getVBlock(block.getLocation());
            if(vBlock == null)
                return;
            vBlock.updateBlockData(block.getBlockData());
            e.setCancelled(!vBlock.isBlockPermissionAllowed(VBlockEventPermission.BLOCK_PISTON_EVENT));
            return;
        }
    }

    @EventHandler
    public void blockPistonRetractEvent(BlockPistonRetractEvent e){
        for (Block block : e.getBlocks()) {
            VBlock vBlock = VCorePaper.getInstance().getCustomBlockManager().getVBlock(block.getLocation());
            if(vBlock == null)
                return;
            vBlock.updateBlockData(block.getBlockData());
            e.setCancelled(!vBlock.isBlockPermissionAllowed(VBlockEventPermission.BLOCK_PISTON_EVENT));
            return;
        }
    }

    @EventHandler
    public void blockBurnEvent(BlockBurnEvent e){
        Block block = e.getBlock();
        VBlock vBlock = VCorePaper.getInstance().getCustomBlockManager().getVBlock(block.getLocation());
        if(vBlock == null)
            return;
        vBlock.updateBlockData(block.getBlockData());
        e.setCancelled(!vBlock.isBlockPermissionAllowed(VBlockEventPermission.BLOCK_DROP_ITEMS_EVENT));
    }

    @EventHandler
    public void save(WorldSaveEvent e){
        threadPool.submit(() -> {
            //long time = System.currentTimeMillis();
            for (Chunk chunk : e.getWorld().getLoadedChunks()) {
                vBlockManager.getDataOfChunk(chunk)
                        //.filter(BlockPersistentData::readyToBeCleaned)
                        .forEach(VBlockSaveFile::save);
                //VCorePaper.getInstance().consoleMessage("&eChunkData unloaded in&7: "+(System.currentTimeMillis() - time)+"ms",true);
            }
        });
    }
}
