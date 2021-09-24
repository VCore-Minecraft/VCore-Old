/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.input;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.09.2021 16:17
 */
public class PlayerWorldSelector<T> {

    private final BiFunction<Player, SelectionResult, T> validateInput;
    private final BiConsumer<Player, T> onFinish;
    private final BiConsumer<Player, SelectionResult> onWrongInput;
    private final Consumer<Player> onCancel;
    private final Consumer<Player> onExpire;


    private final String selectMessage;
    private final String onExpireMessage;
    private final String invalidInputMessage;
    private final String cancelMessage;
    private final String finishMessage;

    private final Plugin plugin;

    private final int expiresAfter;
    private final Runnable onDisconnect;
    private final Player player;
    private final Listener listener;
    private boolean started;
    private T value;
    private BukkitTask timerTask;
    private EndReason end;

    PlayerWorldSelector(Plugin plugin
            , Player player
            , BiFunction<Player, SelectionResult, T> validateInput
            , BiConsumer<Player, T> onFinish
            , BiConsumer<Player, SelectionResult> onWrongInput
            , Consumer<Player> onCancel
            , Consumer<Player> onExpire
            , String selectMessage
            , String onExpireMessage
            , String invalidInputMessage
            , String cancelMessage
            , String finishMessage
            , int expiresAfter
            , Runnable onDisconnect) {
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(player);
        Objects.requireNonNull(validateInput);
        Objects.requireNonNull(onFinish);
        Objects.requireNonNull(onWrongInput);
        Objects.requireNonNull(onCancel);
        Objects.requireNonNull(onExpire);
        Objects.requireNonNull(selectMessage);
        Objects.requireNonNull(onExpireMessage);
        Objects.requireNonNull(invalidInputMessage);
        Objects.requireNonNull(cancelMessage);
        Objects.requireNonNull(finishMessage);
        Objects.requireNonNull(onDisconnect);

        this.validateInput = validateInput;
        this.onFinish = onFinish;
        this.onWrongInput = onWrongInput;
        this.onCancel = onCancel;
        this.onExpire = onExpire;
        this.selectMessage = selectMessage;
        this.onExpireMessage = onExpireMessage;
        this.invalidInputMessage = invalidInputMessage;
        this.cancelMessage = cancelMessage;
        this.plugin = plugin;
        this.finishMessage = finishMessage;
        this.expiresAfter = expiresAfter;
        this.onDisconnect = onDisconnect;
        this.player = player;
        this.listener = new Listener();
    }

    public void start() {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        if (expiresAfter > 0)
            timerTask = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {

                if (!started)
                    return;
                onExpire.accept(player);
                if (onExpireMessage != null)
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', onExpireMessage));
                end(EndReason.RUN_OUT_OF_TIME);
            }, expiresAfter);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', selectMessage));
        started = true;
        end = null;
    }

    public void forceEnd() {
        end(EndReason.CUSTOM);
    }

    private void end(EndReason reason) {
        started = false;
        end = reason;
        unregister();
    }

    private void unregister() {
        // Maybe the timer is still running
        if (timerTask != null)
            timerTask.cancel();
        // Unregister events
        HandlerList.unregisterAll(listener);
    }

    private void executeSelectionPipeline(SelectionResult selectionResult) {
        if (player.isSneaking()) {
            onCancel.accept(player);
            end(EndReason.PLAYER_CANCELS);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', cancelMessage));
            return;
        }
        T selected = validateInput.apply(player, selectionResult);
        if (selected == null) {
            onWrongInput.accept(player, selectionResult);
            end(EndReason.INVALID_INPUT);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', invalidInputMessage));
            return;
        }
        onFinish.accept(player, selected);
        end(EndReason.FINISH);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', finishMessage));
    }

    private enum EndReason {
        /**
         * The input-process ended successfully
         */
        FINISH,
        /**
         * The player ran out of time to select
         */
        RUN_OUT_OF_TIME,
        /**
         * The player disconnected
         */
        PLAYER_DISCONNECTS,
        /**
         * The player cancels
         */
        PLAYER_CANCELS,
        /**
         * The player sent an invalid input and the repeating mode is off
         */
        INVALID_INPUT,
        /**
         * A plugin ended the input process
         */
        CUSTOM
    }

    public static class PlayerWorldSelectorBuilder<T> {
        private final Runnable onDisconnect;
        private Plugin plugin;
        private BiFunction<Player, SelectionResult, T> validateInput;
        private BiConsumer<Player, T> onFinish;
        private BiConsumer<Player, SelectionResult> onWrongInput;
        private Consumer<Player> onCancel;
        private Consumer<Player> onExpire;
        private String selectMessage;
        private String onExpireMessage;
        private String invalidInputMessage;
        private String cancelMessage;
        private String finishMessage;
        private int expiresAfter;

        public PlayerWorldSelectorBuilder() {
            onWrongInput = (player1, selectionResult) -> {
            };

            onCancel = (player1) -> {
            };
            onExpire = (player1) -> {
            };

            selectMessage = "&7Please select something by right clicking. Cancel by &cSneakClick";
            onExpireMessage = "&7Selection expired";
            invalidInputMessage = "&cInvalid selection";
            cancelMessage = "&cSelection cancelled";
            finishMessage = "&aSelection successful";

            expiresAfter = 20 * 10;
            onDisconnect = () -> {
            };
        }

        public PlayerWorldSelector<T> build(Player player) {
            return new PlayerWorldSelector<>(plugin, player, validateInput, onFinish, onWrongInput, onCancel, onExpire, selectMessage, onExpireMessage, invalidInputMessage, cancelMessage, finishMessage, expiresAfter, onDisconnect);
        }

        public PlayerWorldSelectorBuilder<T> plugin(@NotNull Plugin plugin) {
            this.plugin = plugin;
            return this;
        }

        public PlayerWorldSelectorBuilder<T> validateInput(@NotNull BiFunction<Player, SelectionResult, T> validateInput) {
            this.validateInput = validateInput;
            return this;
        }

        public PlayerWorldSelectorBuilder<T> onFinish(@NotNull BiConsumer<Player, T> onFinish) {
            this.onFinish = onFinish;
            return this;
        }

        public PlayerWorldSelectorBuilder<T> onWrongInput(@NotNull BiConsumer<Player, SelectionResult> onWrongInput) {
            this.onWrongInput = onWrongInput;
            return this;
        }

        public PlayerWorldSelectorBuilder<T> onCancel(Consumer<Player> onCancel) {
            this.onCancel = onCancel;
            return this;
        }

        public PlayerWorldSelectorBuilder<T> onExpire(Consumer<Player> onExpire) {
            this.onExpire = onExpire;
            return this;
        }

        public PlayerWorldSelectorBuilder<T> selectMessage(String selectMessage) {
            this.selectMessage = selectMessage;
            return this;
        }

        public PlayerWorldSelectorBuilder<T> onExpireMessage(String onExpireMessage) {
            this.onExpireMessage = onExpireMessage;
            return this;
        }

        public PlayerWorldSelectorBuilder<T> invalidInputMessage(String invalidInputMessage) {
            this.invalidInputMessage = invalidInputMessage;
            return this;
        }

        public PlayerWorldSelectorBuilder<T> cancelMessage(String cancelMessage) {
            this.cancelMessage = cancelMessage;
            return this;
        }

        public PlayerWorldSelectorBuilder<T> finishMessage(String finishMessage) {
            this.finishMessage = finishMessage;
            return this;
        }

        public PlayerWorldSelectorBuilder<T> expiresAfter(int expiresAfter) {
            this.expiresAfter = expiresAfter;
            return this;
        }
    }

    public record SelectionResult(@NotNull Player player, @Nullable Block block, @Nullable Entity entity, @Nullable
    ItemStack stack, boolean sneaking) {
    }

    private class Listener implements org.bukkit.event.Listener {

        @EventHandler
        public void playerInteract(PlayerInteractEvent e) {
            if (!e.getPlayer().equals(player) || !started)
                return;
            e.setCancelled(true);
            executeSelectionPipeline(new SelectionResult(player, e.getClickedBlock(), null, e.getItem(), player.isSneaking()));
        }

        @EventHandler
        public void playerInteractEntity(PlayerInteractEntityEvent e) {
            if (!e.getPlayer().equals(player) || !started)
                return;
            e.setCancelled(true);
            executeSelectionPipeline(new SelectionResult(player, null, e.getRightClicked(), e.getPlayer().getEquipment().getItem(e.getHand()), player.isSneaking()));
        }

        @EventHandler
        public void onPlayerDisconnect(PlayerQuitEvent e) {
            if (e.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                if (!started)
                    return;
                onDisconnect.run();
                end(EndReason.PLAYER_DISCONNECTS);
            }
        }
    }
}
