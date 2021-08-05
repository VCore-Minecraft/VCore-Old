/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.api.bukkit;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.networkmanager.player.VCorePlayer;
import de.verdox.vcore.synchronization.networkmanager.player.api.VCorePlayerAPIImpl;
import de.verdox.vcore.plugin.wrapper.types.GameLocation;
import de.verdox.vcore.plugin.wrapper.types.ServerLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 03.08.2021 20:17
 */
public class VCorePlayerAPIBukkitImpl extends VCorePlayerAPIImpl implements Listener {
    public VCorePlayerAPIBukkitImpl(VCorePlugin.Minecraft plugin) {
        super(plugin);
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    @Override
    public CompletableFuture<ServerLocation> getServerLocation(@Nonnull VCorePlayer vCorePlayer) {
        Player player = Bukkit.getPlayer(vCorePlayer.getObjectUUID());
        if(player == null)
            return super.getServerLocation(vCorePlayer);
        else {
            CompletableFuture<ServerLocation> completableFuture = new CompletableFuture<>();
            completableFuture.complete(convertToServerLocation(player.getLocation()));
            return completableFuture;
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        CompletableFuture<Set<Runnable>> future = vCorePlayerTaskScheduler.getAllTasks(e.getPlayer().getUniqueId());
        plugin.async(() -> {
            try {
                Set<Runnable> tasks = future.get(5, TimeUnit.SECONDS);
                plugin.consoleMessage("&eFound Tasks for &e"+e.getPlayer().getName()+" &b"+tasks.size(),false);
                if(tasks.size() == 0)
                    return;
                plugin.consoleMessage("&eExecuting pending tasks for player &e"+e.getPlayer().getName(),false);
                tasks.forEach(plugin::sync);
            } catch (InterruptedException | ExecutionException | TimeoutException interruptedException) {
                interruptedException.printStackTrace();
            }
        });
    }

    private ServerLocation convertToServerLocation(Location location){
        ServerLocation serverLocation = new ServerLocation();
        serverLocation.serverName = plugin.getCoreInstance().getServerName();
        serverLocation.worldName = location.getWorld().getName();
        serverLocation.x = location.getX();
        serverLocation.y = location.getY();
        serverLocation.z = location.getZ();
        return serverLocation;
    }

    private Location convertToLocation(GameLocation gameLocation){
        World world = Bukkit.getWorld(gameLocation.worldName);
        if (world == null)
            world = Bukkit.getWorlds().get(0);
        return new Location(world,gameLocation.x,gameLocation.y,gameLocation.z);
    }
}
