/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nbt;

import de.verdox.vcore.nbt.block.VBlock;
import de.verdox.vcore.nbt.events.callbacks.BlockDestroyCallback;
import de.verdox.vcore.nbt.events.callbacks.BlockInteractCallback;
import de.verdox.vcore.nbt.events.callbacks.BlockPlaceCallback;
import de.verdox.vcore.nbt.events.callbacks.EventBlockCallback;
import de.verdox.vcore.nbt.items.VCoreItem;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.listener.VCoreListener;
import de.verdox.vcorepaper.VCorePaper;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.bukkit.block.Block;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomDataListener extends VCoreListener.VCoreBukkitListener {

    private final ExecutorService executorService = Executors.newCachedThreadPool(new DefaultThreadFactory("VCoreCustomDataListener"));
    private final VCoreNBTModule vCoreNBTModule;

    public CustomDataListener(VCoreNBTModule vCoreNBTModule, VCorePlugin.Minecraft plugin) {
        super(plugin);
        this.vCoreNBTModule = vCoreNBTModule;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemInteract(PlayerInteractEvent e) {
        if (e.isCancelled())
            return;
        if (e.getHand() != null && !e.getHand().equals(EquipmentSlot.HAND))
            return;

        Action action = e.getAction();
        ItemStack stack = e.getItem();
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        if (block == null)
            return;

        executorService.submit(() -> {
            VBlock.BlockBased vBlock = vCoreNBTModule.getCustomBlockProvider().getBlockDataManager().getVBlock(block);

            VCoreItem vCoreItem = null;
            if (stack != null && !stack.getType().isAir())
                vCoreItem = vCoreNBTModule.getCustomItemManager().wrap(VCoreItem.class, stack);

            VCoreItem finalVCoreItem = vCoreItem;
            if (vCoreItem != null)
                vCoreItem.getCustomDataKeys()
                        .parallelStream()
                        .map(key -> VCorePaper.getInstance().getCustomItemManager().getDataType(key))
                        .filter(Objects::nonNull)
                        .filter(itemCustomData -> itemCustomData instanceof BlockInteractCallback)
                        .forEach(itemCustomData -> ((BlockInteractCallback) itemCustomData).blockCallback(player, action, finalVCoreItem, vBlock, EventBlockCallback.CallbackType.INTERACT_BLOCK));
            if (vBlock.isVBlock())
                vBlock.getCustomDataKeys()
                        .parallelStream()
                        .map(key -> VCorePaper.getInstance().getCustomBlockDataManager().getDataType(key))
                        .filter(Objects::nonNull)
                        .filter(itemCustomData -> itemCustomData instanceof BlockInteractCallback)
                        .forEach(itemCustomData -> ((BlockInteractCallback) itemCustomData).blockCallback(player, action, finalVCoreItem, vBlock, EventBlockCallback.CallbackType.INTERACT_BLOCK));
            VBlock.LocationBased vBlockLocBased = vBlock.asLocationBased();
            if (vBlockLocBased.isVBlock())
                vBlockLocBased
                        .getCustomDataKeys()
                        .parallelStream()
                        .map(key -> VCorePaper.getInstance().getCustomLocationDataManager().getDataType(key))
                        .filter(Objects::nonNull)
                        .filter(vBlockCustomData -> vBlockCustomData instanceof BlockInteractCallback)
                        .forEach(vBlockCustomData -> ((BlockPlaceCallback) vBlockCustomData).blockCallback(player, Action.RIGHT_CLICK_BLOCK, finalVCoreItem, vBlockLocBased, EventBlockCallback.CallbackType.PLACE_BLOCK));

        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlaceBlock(BlockPlaceEvent e) {
        if (e.isCancelled())
            return;

        Player player = e.getPlayer();
        ItemStack stack = e.getItemInHand();
        Block block = e.getBlock();

        executorService.submit(() -> {
            VBlock.BlockBased vBlock = vCoreNBTModule.getCustomBlockProvider().getBlockDataManager().getVBlock(block);

            VCoreItem vCoreItem = null;
            if (!stack.getType().isAir())
                vCoreItem = vCoreNBTModule.getCustomItemManager().wrap(VCoreItem.class, stack);

            VCoreItem finalVCoreItem = vCoreItem;
            if (vCoreItem != null)
                vCoreItem.getCustomDataKeys()
                        .parallelStream()
                        .map(key -> VCorePaper.getInstance().getCustomItemManager().getDataType(key))
                        .filter(Objects::nonNull)
                        .filter(itemCustomData -> itemCustomData instanceof BlockPlaceCallback)
                        .forEach(itemCustomData -> ((BlockPlaceCallback) itemCustomData).blockCallback(player, Action.RIGHT_CLICK_BLOCK, finalVCoreItem, vBlock, EventBlockCallback.CallbackType.PLACE_BLOCK));

            if (vBlock.isVBlock())
                vBlock.getCustomDataKeys()
                        .parallelStream()
                        .map(key -> VCorePaper.getInstance().getCustomBlockDataManager().getDataType(key))
                        .filter(Objects::nonNull)
                        .filter(vBlockCustomData -> vBlockCustomData instanceof BlockPlaceCallback)
                        .forEach(vBlockCustomData -> ((BlockPlaceCallback) vBlockCustomData).blockCallback(player, Action.RIGHT_CLICK_BLOCK, finalVCoreItem, vBlock, EventBlockCallback.CallbackType.PLACE_BLOCK));
            VBlock.LocationBased vBlockLocBased = vBlock.asLocationBased();
            if (vBlockLocBased.isVBlock())
                vBlockLocBased
                        .getCustomDataKeys()
                        .parallelStream()
                        .map(key -> VCorePaper.getInstance().getCustomLocationDataManager().getDataType(key))
                        .filter(Objects::nonNull)
                        .filter(vBlockCustomData -> vBlockCustomData instanceof BlockPlaceCallback)
                        .forEach(vBlockCustomData -> ((BlockPlaceCallback) vBlockCustomData).blockCallback(player, Action.RIGHT_CLICK_BLOCK, finalVCoreItem, vBlockLocBased, EventBlockCallback.CallbackType.PLACE_BLOCK));

        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDestroyBlock(BlockBreakEvent e) {
        if (e.isCancelled())
            return;

        Player player = e.getPlayer();
        ItemStack stack = player.getInventory().getItemInMainHand();
        Block block = e.getBlock();

        executorService.submit(() -> {
            VBlock.BlockBased vBlock = vCoreNBTModule.getCustomBlockProvider().getBlockDataManager().getVBlock(block);

            VCoreItem vCoreItem = null;
            if (!stack.getType().isAir())
                vCoreItem = vCoreNBTModule.getCustomItemManager().wrap(VCoreItem.class, stack);

            VCoreItem finalVCoreItem = vCoreItem;
            if (vCoreItem != null)
                vCoreItem.getCustomDataKeys()
                        .parallelStream()
                        .map(key -> VCorePaper.getInstance().getCustomItemManager().getDataType(key))
                        .filter(Objects::nonNull)
                        .filter(itemCustomData -> itemCustomData instanceof BlockDestroyCallback)
                        .forEach(itemCustomData -> {
                            ((BlockDestroyCallback) itemCustomData).blockCallback(player, Action.LEFT_CLICK_BLOCK, finalVCoreItem, vBlock, EventBlockCallback.CallbackType.BREAK_BLOCK);
                        });
            if (vBlock.isVBlock())
                vBlock.getCustomDataKeys()
                        .parallelStream()
                        .map(key -> VCorePaper.getInstance().getCustomBlockDataManager().getDataType(key))
                        .filter(Objects::nonNull)
                        .filter(vBlockCustomData -> vBlockCustomData instanceof BlockDestroyCallback)
                        .forEach(vBlockCustomData -> ((BlockDestroyCallback) vBlockCustomData).blockCallback(player, Action.LEFT_CLICK_BLOCK, finalVCoreItem, vBlock, EventBlockCallback.CallbackType.BREAK_BLOCK));
            VBlock.LocationBased vBlockLocBased = vBlock.asLocationBased();
            if (vBlock.isVBlock())
                vBlockLocBased
                        .getCustomDataKeys()
                        .parallelStream()
                        .map(key -> VCorePaper.getInstance().getCustomLocationDataManager().getDataType(key))
                        .filter(Objects::nonNull)
                        .filter(vBlockCustomData -> vBlockCustomData instanceof BlockDestroyCallback)
                        .forEach(vBlockCustomData -> ((BlockDestroyCallback) vBlockCustomData).blockCallback(player, Action.LEFT_CLICK_BLOCK, finalVCoreItem, vBlockLocBased, EventBlockCallback.CallbackType.BREAK_BLOCK));

        });
    }
}
