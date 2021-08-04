/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.api.bukkit;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.networkmanager.player.VCorePlayer;
import de.verdox.vcore.synchronization.networkmanager.player.api.VCorePlayerAPIImpl;
import de.verdox.vcore.synchronization.networkmanager.player.api.querytypes.GameLocation;
import de.verdox.vcore.synchronization.networkmanager.player.api.querytypes.ServerLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 03.08.2021 20:17
 */
public class VCorePlayerAPIBukkitImpl extends VCorePlayerAPIImpl implements Listener {

    private Map<UUID,ServerLocation> teleportCache = new ConcurrentHashMap<>();

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
        if(!teleportCache.containsKey(e.getPlayer().getUniqueId()))
            return;
        Location location = convertToLocation(teleportCache.get(e.getPlayer().getUniqueId()));
        e.getPlayer().teleportAsync(location);
    }

    @Override
    public Object[] respondToQuery(UUID queryUUID, String[] parameters, Object[] queryData) {
        if(locationQueryCache.containsKey(queryUUID))
            return null;
        if(parameters.length != 2)
            return null;

        if(!parameters[0].equals(APIParameters.QUERY.getParameter()))
            return null;

        if(parameters[1].equals(APIParameters.PLAYER_LOCATION_QUERY.getParameter())){
            UUID playerUUID = (UUID) queryData[0];
            Player player = Bukkit.getPlayer(playerUUID);
            if(player == null)
                return null;
            return new Object[]{plugin.getCoreInstance().getServerName(),player.getLocation().getWorld().getName(),player.getLocation().getX(),player.getLocation().getY(),player.getLocation().getZ()};
        }
        else if(parameters[1].equals(APIParameters.PLAYER_TELEPORT.getParameter())){
            UUID playerUUID = (UUID) queryData[0];

            ServerLocation serverLocation = new ServerLocation();
            serverLocation.serverName = (String) queryData[1];
            serverLocation.worldName = (String) queryData[2];
            serverLocation.x = (double) queryData[3];
            serverLocation.y = (double) queryData[4];
            serverLocation.z = (double) queryData[5];

            if(!serverLocation.serverName.equals(plugin.getCoreInstance().getServerName()))
                return null;

            Player player = Bukkit.getPlayer(playerUUID);
            if(player == null) {
                teleportCache.put(playerUUID,serverLocation);
                return null;
            }
            else {
                plugin.sync(() -> player.teleportAsync(convertToLocation(serverLocation)));
            }

        }
        return null;
    }

    @Override
    public void onResponse(UUID queryUUID, String[] parameters, Object[] queryData, Object[] responseData) {
        if(!parameters[0].equals(APIParameters.QUERY.getParameter()))
            return;
        if(parameters[1].equals(APIParameters.PLAYER_LOCATION_QUERY.getParameter())){
            if(locationQueryCache.containsKey(queryUUID)){
                if(responseData.length != 5)
                    throw new IllegalStateException("ResponseData does not contain all data needed!");
                ServerLocation serverLocation = new ServerLocation();
                serverLocation.serverName = (String) responseData[0];
                serverLocation.worldName = (String) responseData[1];
                serverLocation.x = (double) responseData[2];
                serverLocation.y = (double) responseData[3];
                serverLocation.z = (double) responseData[4];
                locationQueryCache.get(queryUUID).complete(serverLocation);
            }
        }
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
