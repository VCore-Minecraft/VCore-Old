/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.gui.book;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcorepaper.custom.gui.book.event.PlayerOpenBookGUIEvent;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 13.09.2021 21:35
 */
public class BookGUI {
    private final VCorePlugin.Minecraft plugin;
    private final Player player;
    private final BookGUIListener listener;
    private final UUID guiUUID = UUID.randomUUID();
    private final Map<String, Consumer<Player>> callbackCache = new ConcurrentHashMap<>();
    private Book book;

    public BookGUI(@NotNull VCorePlugin.Minecraft plugin, @NotNull Player player) {
        this.plugin = plugin;
        this.player = player;
        this.listener = new BookGUIListener();
    }

    public void provideBook(@NotNull Supplier<Book> bookSupplier) {
        book = bookSupplier.get();
    }

    public void openBook() {
        if (book == null)
            return;
        // TODO: Wenn Spieler neues Buch öffnet, können alle alten geschlossen werden
        PlayerOpenBookGUIEvent playerOpenBookGUIEvent = new PlayerOpenBookGUIEvent(player, this);
        Bukkit.getPluginManager().callEvent(playerOpenBookGUIEvent);
        if (playerOpenBookGUIEvent.isCancelled())
            return;
        player.openBook(book);
        Bukkit.getPluginManager().registerEvents(this.listener, this.plugin);
    }

    public void cleaUp() {
        HandlerList.unregisterAll(this.listener);
        callbackCache.clear();
    }

    public TextComponent createResponsiveCallbackText(@NotNull TextComponent textComponent, @NotNull Consumer<Player> playerConsumer) {
        UUID uuid = UUID.randomUUID();
        String cacheCommand = guiUUID + " " + uuid;
        callbackCache.put(cacheCommand, playerConsumer);
        return textComponent.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/" + cacheCommand));
    }

    private class BookGUIListener implements Listener {
        @EventHandler
        public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
            if (!e.getPlayer().equals(player))
                return;
            String command = e.getMessage().replace("/", "");
            if (!callbackCache.containsKey(command))
                return;
            e.setCancelled(true);
            callbackCache.get(command).accept(e.getPlayer());
            cleaUp();
        }

        @EventHandler
        public void onBookOpen(PlayerOpenBookGUIEvent e) {
            Player player = e.getPlayer();
            // An other Book GUI is opened
            if (BookGUI.this.player.equals(player) && !BookGUI.this.equals(e.getBookGUI()))
                cleaUp();
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            Player player = (Player) e.getWhoClicked();
            // Player can't click in Inventory and have an book opened in parallel
            if (player.equals(BookGUI.this.player))
                cleaUp();
        }

        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent e) {
            Player player = e.getPlayer();

            if (player.equals(BookGUI.this.player))
                cleaUp();
        }

        @EventHandler
        public void playerKick(PlayerKickEvent e) {
            Player player = e.getPlayer();

            if (player.equals(BookGUI.this.player))
                cleaUp();
        }

        @EventHandler
        public void onDeath(PlayerDeathEvent e) {
            Player player = e.getEntity();

            if (player.equals(BookGUI.this.player))
                cleaUp();
        }
    }
}
