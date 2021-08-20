/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.events.paper;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.listener.VCoreListener;
import de.verdox.vcore.util.VCoreUtil;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.events.paper.blockevents.chestevents.ChestMergeEvent;
import de.verdox.vcorepaper.custom.events.paper.blockevents.chestevents.ChestSplitEvent;
import de.verdox.vcorepaper.custom.events.paper.playerevents.PlayerMilkCowEvent;
import de.verdox.vcorepaper.custom.events.paper.playerevents.PlayerPreOpenContainerEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Set;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.08.2021 01:07
 */
public class CustomPaperEventListener extends VCoreListener.VCoreBukkitListener {
    public CustomPaperEventListener(VCorePlugin.Minecraft plugin) {
        super(plugin);
    }

    @EventHandler
    public void onMilk(PlayerInteractEntityEvent e) {
        if (e instanceof PlayerMilkCowEvent)
            return;
        Player player = e.getPlayer();
        if (!(e.getRightClicked() instanceof Cow))
            return;
        Cow cow = (Cow) e.getRightClicked();
        PlayerMilkCowEvent playerMilkCowEvent = null;
        if (player.getInventory().getItemInMainHand().getType().equals(Material.BUCKET)) {
            playerMilkCowEvent = new PlayerMilkCowEvent(player, cow, EquipmentSlot.HAND);
        } else if (player.getInventory().getItemInOffHand().getType().equals(Material.BUCKET)) {
            playerMilkCowEvent = new PlayerMilkCowEvent(player, cow, EquipmentSlot.OFF_HAND);
        }
        if (playerMilkCowEvent != null) {
            plugin.consoleMessage("&8[&eEvent&7-&eDebug&8] &7Calling &b" + PlayerMilkCowEvent.class.getSimpleName(), true);
            Bukkit.getPluginManager().callEvent(playerMilkCowEvent);
            if (playerMilkCowEvent.isCancelled())
                e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void callChestMergeEvent(BlockPlaceEvent e) {
        if (!e.getBlock().getType().equals(Material.CHEST))
            return;
        Chest chest = (Chest) e.getBlock().getState();
        Bukkit.getScheduler().runTaskLater(VCorePaper.getInstance(), () -> {
            if (chest.getBlockInventory().getHolder() instanceof DoubleChest) {
                DoubleChest doubleChest = (DoubleChest) chest.getBlockInventory().getHolder();
                Chest chestConnectedTo = VCoreUtil.BukkitUtil.getBukkitWorldUtil().findConnectedChest(chest).stream().filter(chest1 -> !chest1.equals(chest)).findAny().orElseThrow(() -> new IllegalStateException("Could not find connected chest. Might be an API Bug"));
                plugin.consoleMessage("&8[&eEvent&7-&eDebug&8] &7Calling &b" + ChestMergeEvent.class.getSimpleName(), true);
                Bukkit.getPluginManager().callEvent(new ChestMergeEvent(e.getPlayer(), chest, chestConnectedTo, doubleChest));
            }
        }, 5);
    }

    @EventHandler(ignoreCancelled = true)
    public void callPreOpenContainerEvent(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                || e.getClickedBlock() == null
                || !(e.getClickedBlock().getState() instanceof Container))
            return;
        plugin.consoleMessage("&8[&eEvent&7-&eDebug&8] &7Calling &b" + PlayerPreOpenContainerEvent.class.getSimpleName(), true);
        e.setCancelled(!VCoreUtil.BukkitUtil.getBukkitServerUtil().callCancellable(new PlayerPreOpenContainerEvent(e.getPlayer(), (Container) e.getClickedBlock().getState())));
    }

    @EventHandler(ignoreCancelled = true)
    public void callChestSplitEvent(BlockBreakEvent e) {
        if (!e.getBlock().getType().equals(Material.CHEST))
            return;
        Chest chest = (Chest) e.getBlock().getState();
        if (!(chest.getBlockInventory().getHolder() instanceof DoubleChest))
            return;
        DoubleChest doubleChest = (DoubleChest) chest.getBlockInventory().getHolder();
        Set<Chest> foundChests = VCoreUtil.BukkitUtil.getBukkitWorldUtil().findConnectedChest(chest);
        Chest otherChest = foundChests.stream().filter(chest1 -> !chest1.equals(chest)).findAny().orElseThrow(() -> new IllegalStateException("Could not find connected chest. Might be an API Bug"));
        plugin.consoleMessage("&8[&eEvent&7-&eDebug&8] &7Calling &b" + ChestSplitEvent.class.getSimpleName(), true);
        e.setCancelled(!VCoreUtil.BukkitUtil.getBukkitServerUtil().callCancellable(new ChestSplitEvent(e.getPlayer(), otherChest, chest, doubleChest)));
    }

    //private boolean callBlockChangeState(Block block, boolean materialChanged){
    //    BlockChangeStateEvent blockChangeStateEvent = new BlockChangeStateEvent(block,materialChanged);
    //    Bukkit.getPluginManager().callEvent(blockChangeStateEvent);
    //    return blockChangeStateEvent.isCancelled();
    //}
//
    //@EventHandler(priority = EventPriority.HIGHEST)
    //public void blockBreak(BlockBreakEvent e){
    //    if(e.isCancelled())
    //        return;
    //    e.setCancelled(callBlockChangeState(e.getBlock(),true));
    //}
//
    //@EventHandler(priority = EventPriority.HIGHEST)
    //public void blockPlace(BlockPlaceEvent e){
    //    if(e.isCancelled())
    //        return;
    //    e.setCancelled(callBlockChangeState(e.getBlock(),true));
    //}
//
    //@EventHandler
    //public void pistonRetract(BlockPistonRetractEvent e){
    //    if(e.isCancelled())
    //        return;
    //    for (Block block : e.getBlocks()) {
    //        if(callBlockChangeState(block,true)) {
    //            e.setCancelled(true);
    //            return;
    //        }
    //    }
    //}
//
    //@EventHandler
    //public void pistonExtend(BlockPistonExtendEvent e){
    //    if(e.isCancelled())
    //        return;
    //    for (Block block : e.getBlocks()) {
    //        if(callBlockChangeState(block,true)) {
    //            e.setCancelled(true);
    //            return;
    //        }
    //    }
    //}
//
    //@EventHandler
    //public void blockFromTo(BlockFromToEvent e){
    //    if(e.isCancelled())
    //        return;
    //    e.setCancelled(callBlockChangeState(e.getBlock(),true));
    //}
//
    //@EventHandler
    //public void explodeEvent(BlockExplodeEvent e){
    //    if(e.isCancelled())
    //        return;
    //    e.setCancelled(callBlockChangeState(e.getBlock(),true));
    //}
//
    //@EventHandler
    //public void entityChangeBlock(EntityChangeBlockEvent e){
    //    if(e.isCancelled())
    //        return;
    //    e.setCancelled(callBlockChangeState(e.getBlock(),true));
    //}
//
    //@EventHandler
    //public void interact(PlayerInteractEvent e){
    //    if(e.isCancelled())
    //        return;
    //    if(e.getAction().equals(Action.PHYSICAL)){
    //        Block block = e.getClickedBlock();
    //        if(block == null)
    //            return;
    //        e.setCancelled(callBlockChangeState(e.getClickedBlock(),block.getType().equals(Material.FARMLAND)));
    //    }
    //}
}
