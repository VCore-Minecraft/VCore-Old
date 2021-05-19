package de.verdox.vcorepaper.custom;

import de.verdox.vcore.concurrent.ThreadObjectManager;
import de.verdox.vcore.event.listener.VCoreListener;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.util.keys.ChunkKey;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.blocks.VBlock;
import de.verdox.vcorepaper.custom.events.callbacks.BlockDestroyCallback;
import de.verdox.vcorepaper.custom.events.callbacks.BlockPlaceCallback;
import de.verdox.vcorepaper.custom.items.VCoreItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ExecutorService;

public class CustomDataListener extends VCoreListener.VCoreBukkitListener {

    private final ThreadObjectManager<ChunkKey> threadObjectManager = new ThreadObjectManager<>();

    public CustomDataListener(VCorePlugin.Minecraft plugin) {
        super(plugin);
    }

    @EventHandler
    public void onItemInteract(PlayerInteractEvent e){
        ItemStack stack = e.getItem();
        Action action = e.getAction();

    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent e){
        Player player = e.getPlayer();
        ItemStack stack = e.getItemInHand();

        if(e.isCancelled())
            return;

        threadObjectManager.submitTask(new ChunkKey(e.getBlock().getChunk()),() -> {
            VCoreItem vCoreItem = VCorePaper.getInstance().getCustomItemManager().wrap(VCoreItem.class,stack);
            VBlock vBlock = VCorePaper.getInstance().getVBlockManager().wrap(VBlock.class, e.getBlock().getState());

            vCoreItem.getCustomDataKeys()
                    .parallelStream()
                    .map(key -> VCorePaper.getInstance().getCustomItemManager().getDataType(key))
                    .filter(itemCustomData -> itemCustomData instanceof BlockPlaceCallback)
                    .forEach(itemCustomData -> ((BlockPlaceCallback) itemCustomData).blockCallback(player,vCoreItem,vBlock,e.isCancelled()));

            vBlock.getCustomDataKeys()
                    .parallelStream()
                    .map(key -> VCorePaper.getInstance().getVBlockManager().getDataType(key))
                    .filter(vBlockCustomData -> vBlockCustomData instanceof BlockPlaceCallback)
                    .forEach(vBlockCustomData -> ((BlockPlaceCallback) vBlockCustomData).blockCallback(player,vCoreItem,vBlock,e.isCancelled()));
        });
    }

    @EventHandler
    public void onDestroyBlock(BlockBreakEvent e){
        Player player = e.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if(e.isCancelled())
            return;

        threadObjectManager.submitTask(new ChunkKey(e.getBlock().getChunk()),() -> {
            VCoreItem vCoreItem = VCorePaper.getInstance().getCustomItemManager().wrap(VCoreItem.class,itemInHand);
            VBlock vBlock = VCorePaper.getInstance().getVBlockManager().wrap(VBlock.class, e.getBlock().getState());

            vCoreItem.getCustomDataKeys()
                    .parallelStream()
                    .map(key -> VCorePaper.getInstance().getCustomItemManager().getDataType(key))
                    .filter(itemCustomData -> itemCustomData instanceof BlockDestroyCallback)
                    .forEach(itemCustomData -> ((BlockDestroyCallback) itemCustomData).blockCallback(player,vCoreItem,vBlock,e.isCancelled()));

            vBlock.getCustomDataKeys()
                    .parallelStream()
                    .map(key -> VCorePaper.getInstance().getVBlockManager().getDataType(key))
                    .filter(vBlockCustomData -> vBlockCustomData instanceof BlockDestroyCallback)
                    .forEach(vBlockCustomData -> ((BlockDestroyCallback) vBlockCustomData).blockCallback(player,vCoreItem,vBlock,e.isCancelled()));
        });
    }
}
