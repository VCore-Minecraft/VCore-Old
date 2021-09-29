/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.api.bukkit;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.wrapper.types.GameLocation;
import de.verdox.vcore.plugin.wrapper.types.ServerLocation;
import de.verdox.vcore.synchronization.networkmanager.player.VCorePlayer;
import de.verdox.vcore.synchronization.networkmanager.player.api.VCorePlayerAPIImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 03.08.2021 20:17
 */
public class VCorePlayerAPIBukkitImpl extends VCorePlayerAPIImpl implements Listener {
    private final VCorePlugin.Minecraft plugin;

    public VCorePlayerAPIBukkitImpl(@NotNull VCorePlugin.Minecraft plugin) {
        super(plugin);
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public CompletableFuture<ServerLocation> getServerLocation(@Nonnull @NotNull VCorePlayer vCorePlayer) {
        Player player = Bukkit.getPlayer(vCorePlayer.getObjectUUID());
        if (player == null)
            return super.getServerLocation(vCorePlayer);
        else {
            CompletableFuture<ServerLocation> completableFuture = new CompletableFuture<>();
            completableFuture.complete(convertToServerLocation(player.getLocation()));
            return completableFuture;
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        CompletableFuture<Set<Runnable>> future = vCorePlayerTaskScheduler.getAllTasks(e.getPlayer().getUniqueId());
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin.getPlugin(), () -> {
            try {
                if (Bukkit.getPlayer(e.getPlayer().getUniqueId()) == null)
                    return;
                Set<Runnable> tasks = future.get(5, TimeUnit.SECONDS);
                plugin.consoleMessage("&eFound Tasks for &e" + e.getPlayer().getName() + " &b" + tasks.size(), false);
                if (tasks.size() == 0)
                    return;
                plugin.consoleMessage("&eExecuting pending tasks for player &e" + e.getPlayer().getName(), false);
                tasks.forEach(plugin::sync);
            } catch (InterruptedException | ExecutionException | TimeoutException interruptedException) {
                interruptedException.printStackTrace();
            }
        }, 10L);
    }

    private ServerLocation convertToServerLocation(Location location) {
        return new ServerLocation(plugin.getCoreInstance().getServerName(), location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
    }

    private Location convertToLocation(GameLocation gameLocation) {
        World world = Bukkit.getWorld(gameLocation.worldName);
        if (world == null)
            world = Bukkit.getWorlds().get(0);
        return new Location(world, gameLocation.x, gameLocation.y, gameLocation.z);
    }
}
