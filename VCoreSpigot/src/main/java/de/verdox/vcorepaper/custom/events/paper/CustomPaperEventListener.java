/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.events.paper;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.listener.VCoreListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

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
            Bukkit.getPluginManager().callEvent(playerMilkCowEvent);
            if (playerMilkCowEvent.isCancelled())
                e.setCancelled(true);
        }
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
