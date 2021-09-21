/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.gui;

import de.verdox.vcore.plugin.bukkit.BukkitPlugin;
import de.verdox.vcorepaper.VCorePaper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 01.09.2021 18:27
 */
public class ObservableInventory implements CustomGUI {

    private final ObservableInventoryListener listener;
    private final BukkitPlugin plugin;
    private final Inventory inventory;
    private final Player player;
    private final Consumer<List<ItemStack>> onClose;
    private boolean open;

    public ObservableInventory(BukkitPlugin plugin, Player player, Inventory inventory, Consumer<List<ItemStack>> onClose) {
        this.plugin = plugin;
        this.inventory = inventory;
        this.player = player;
        this.onClose = onClose;
        this.listener = new ObservableInventoryListener();
    }

    @Override
    public void openInventory() {
        Bukkit.getPluginManager().registerEvents(this.listener, this.plugin);
        VCorePaper.getInstance().sync(() -> player.openInventory(inventory));
        this.open = true;
    }

    private void closeInventory() {
        if (open) {
            open = false;
            HandlerList.unregisterAll(this.listener);
            if (onClose != null)
                onClose.accept(Arrays.stream(inventory.getContents()).filter(Objects::nonNull).collect(Collectors.toList()));
        }
    }

    private class ObservableInventoryListener implements Listener {
        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e) {
            if (open && e.getInventory().equals(inventory)) {
                closeInventory();
            }
        }
    }
}
