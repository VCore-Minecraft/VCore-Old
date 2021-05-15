package de.verdox.vcorepaper.custom.items;

import de.verdox.vcore.event.listener.VCoreListener;
import de.verdox.vcore.plugin.VCorePlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CustomItemListener extends VCoreListener.VCoreBukkitListener {
    public CustomItemListener(VCorePlugin.Minecraft plugin) {
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
    }

    @EventHandler
    public void onDestroyBlock(BlockBreakEvent e){
        Player player = e.getPlayer();

    }
}
