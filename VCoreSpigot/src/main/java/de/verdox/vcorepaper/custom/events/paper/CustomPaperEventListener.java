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
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
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
    public void onMilk(PlayerInteractEntityEvent e){
        if(e instanceof PlayerMilkCowEvent)
            return;
        Player player = e.getPlayer();
        if(!(e.getRightClicked() instanceof Cow))
            return;
        Cow cow = (Cow) e.getRightClicked();
        PlayerMilkCowEvent playerMilkCowEvent = null;
        if(player.getInventory().getItemInMainHand().getType().equals(Material.BUCKET)) {
            playerMilkCowEvent = new PlayerMilkCowEvent(player, cow, EquipmentSlot.HAND);
        }
        else if(player.getInventory().getItemInOffHand().getType().equals(Material.BUCKET)) {
            playerMilkCowEvent = new PlayerMilkCowEvent(player, cow, EquipmentSlot.OFF_HAND);
        }
        if(playerMilkCowEvent != null){
            Bukkit.getPluginManager().callEvent(playerMilkCowEvent);
            if(playerMilkCowEvent.isCancelled())
                e.setCancelled(true);
        }
    }
}
