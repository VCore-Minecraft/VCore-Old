package de.verdox.vcorepaper.custom;

import de.verdox.vcore.concurrent.CatchingRunnable;
import de.verdox.vcore.concurrent.ThreadObjectManager;
import de.verdox.vcore.event.listener.VCoreListener;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.util.keys.ChunkKey;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.blocks.VBlock;
import de.verdox.vcorepaper.custom.events.callbacks.BlockDestroyCallback;
import de.verdox.vcorepaper.custom.events.callbacks.BlockInteractCallback;
import de.verdox.vcorepaper.custom.events.callbacks.BlockPlaceCallback;
import de.verdox.vcorepaper.custom.events.callbacks.EventBlockCallback;
import de.verdox.vcorepaper.custom.items.VCoreItem;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class CustomDataListener extends VCoreListener.VCoreBukkitListener {

    private final ThreadObjectManager<ChunkKey> threadObjectManager = new ThreadObjectManager<>();

    public CustomDataListener(VCorePlugin.Minecraft plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemInteract(PlayerInteractEvent e){
        Player player = e.getPlayer();
        ItemStack stack = e.getItem();
        Action action = e.getAction();

        if(e.isCancelled())
            return;
        if(e.getHand() != null && !e.getHand().equals(EquipmentSlot.HAND))
            return;

        Block block = e.getClickedBlock();
        if(block == null)
            return;
        BlockData blockData = e.getClickedBlock().getBlockData();

        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(),() -> { });

        threadObjectManager.submitTask(new ChunkKey(block.getChunk()),new CatchingRunnable(() -> {
            VBlock vBlock = VCorePaper.getInstance().getCustomBlockManager().wrap(VBlock.class, block.getState());
            vBlock.updateBlockData(blockData);

            VCoreItem vCoreItem = null;
            if(stack != null && !stack.getType().isAir())
                vCoreItem = VCorePaper.getInstance().getCustomItemManager().wrap(VCoreItem.class,stack);

            VCoreItem finalVCoreItem = vCoreItem;
            if(vCoreItem != null)
                vCoreItem.getCustomDataKeys()
                        .parallelStream()
                        .map(key -> VCorePaper.getInstance().getCustomItemManager().getDataType(key))
                        .filter(Objects::nonNull)
                        .filter(itemCustomData -> itemCustomData instanceof BlockInteractCallback)
                        .forEach(itemCustomData -> ((BlockInteractCallback) itemCustomData).blockCallback(player,action,finalVCoreItem,vBlock, EventBlockCallback.CallbackType.INTERACT_BLOCK));

            vBlock.getCustomDataKeys()
                    .parallelStream()
                    .map(key -> VCorePaper.getInstance().getCustomBlockManager().getDataType(key))
                    .filter(Objects::nonNull)
                    .filter(itemCustomData -> itemCustomData instanceof BlockInteractCallback)
                    .forEach(itemCustomData -> ((BlockInteractCallback) itemCustomData).blockCallback(player, action, finalVCoreItem, vBlock, EventBlockCallback.CallbackType.INTERACT_BLOCK));
        }));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlaceBlock(BlockPlaceEvent e){
        Player player = e.getPlayer();
        ItemStack stack = e.getItemInHand();
        Block block = e.getBlock();
        BlockData blockData = e.getBlock().getBlockData();

        if(e.isCancelled())
            return;

        threadObjectManager.submitTask(new ChunkKey(block.getChunk()),new CatchingRunnable(() -> {
            VBlock vBlock = VCorePaper.getInstance().getCustomBlockManager().wrap(VBlock.class, block.getState());
            vBlock.updateBlockData(blockData);

            VCoreItem vCoreItem = null;
            if(!stack.getType().isAir())
                vCoreItem = VCorePaper.getInstance().getCustomItemManager().wrap(VCoreItem.class,stack);

            VCoreItem finalVCoreItem = vCoreItem;
            if(vCoreItem != null)
                vCoreItem.getCustomDataKeys()
                        .parallelStream()
                        .map(key -> VCorePaper.getInstance().getCustomItemManager().getDataType(key))
                        .filter(Objects::nonNull)
                        .filter(itemCustomData -> itemCustomData instanceof BlockPlaceCallback)
                        .forEach(itemCustomData -> ((BlockPlaceCallback) itemCustomData).blockCallback(player, Action.RIGHT_CLICK_BLOCK,finalVCoreItem,vBlock,EventBlockCallback.CallbackType.PLACE_BLOCK));

            vBlock.getCustomDataKeys()
                    .parallelStream()
                    .map(key -> VCorePaper.getInstance().getCustomBlockManager().getDataType(key))
                    .filter(Objects::nonNull)
                    .filter(vBlockCustomData -> vBlockCustomData instanceof BlockPlaceCallback)
                    .forEach(vBlockCustomData -> ((BlockPlaceCallback) vBlockCustomData).blockCallback(player, Action.RIGHT_CLICK_BLOCK,finalVCoreItem,vBlock,EventBlockCallback.CallbackType.PLACE_BLOCK));
        }));

        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(),() -> { });
    }

    @EventHandler
    public void onDestroyBlock(BlockBreakEvent e){
        Player player = e.getPlayer();
        ItemStack stack = player.getInventory().getItemInMainHand();
        Block block = e.getBlock();
        BlockData blockData = e.getBlock().getBlockData();

        if(e.isCancelled())
            return;

        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(),() -> {
            VBlock vBlock = VCorePaper.getInstance().getCustomBlockManager().wrap(VBlock.class, block.getState());
            vBlock.updateBlockData(blockData);

            VCoreItem vCoreItem = null;
            if(!stack.getType().isAir())
                vCoreItem = VCorePaper.getInstance().getCustomItemManager().wrap(VCoreItem.class,stack);

            VCoreItem finalVCoreItem = vCoreItem;
            if(vCoreItem != null)
                vCoreItem.getCustomDataKeys()
                        .parallelStream()
                        .map(key -> VCorePaper.getInstance().getCustomItemManager().getDataType(key))
                        .filter(Objects::nonNull)
                        .filter(itemCustomData -> itemCustomData instanceof BlockDestroyCallback)
                        .forEach(itemCustomData -> {
                            ((BlockDestroyCallback) itemCustomData).blockCallback(player, Action.LEFT_CLICK_BLOCK, finalVCoreItem, vBlock, EventBlockCallback.CallbackType.BREAK_BLOCK);
                        });
            try{
                vBlock.getCustomDataKeys()
                        .parallelStream()
                        .map(key -> VCorePaper.getInstance().getCustomBlockManager().getDataType(key))
                        .filter(Objects::nonNull)
                        .filter(vBlockCustomData -> vBlockCustomData instanceof BlockDestroyCallback)
                        .forEach(vBlockCustomData -> ((BlockDestroyCallback) vBlockCustomData).blockCallback(player, Action.LEFT_CLICK_BLOCK,finalVCoreItem,vBlock,EventBlockCallback.CallbackType.BREAK_BLOCK));
            }
            finally {
                vBlock.deleteData();
            }
        });
    }
}
