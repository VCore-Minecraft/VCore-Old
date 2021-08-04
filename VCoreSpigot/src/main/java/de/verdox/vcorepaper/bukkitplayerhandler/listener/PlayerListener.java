/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.bukkitplayerhandler.listener;

import com.google.common.eventbus.Subscribe;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import de.verdox.vcore.synchronization.pipeline.player.events.PlayerPreSessionLoadEvent;
import de.verdox.vcore.synchronization.pipeline.player.events.PlayerSessionLoadedEvent;
import de.verdox.vcore.plugin.listener.VCoreListener;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import de.verdox.vcore.util.VCoreUtil;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.bukkitplayerhandler.playerdata.PlayerHandlerData;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 20.06.2021 00:46
 */
public class PlayerListener extends VCoreListener.VCoreBukkitListener {

    private ConcurrentHashMap.KeySetView<Player,Boolean> freezedPlayers = ConcurrentHashMap.newKeySet();
    private Map<UUID, Long> lastSync = new ConcurrentHashMap<>();

    public PlayerListener(VCoreSubsystem<VCorePlugin.Minecraft> subsystem) {
        super(subsystem);
    }

    @Subscribe
    public void onSessionPreLoad(PlayerPreSessionLoadEvent e){
        Player player = Bukkit.getPlayer(e.getPlayerUUID());
        freezedPlayers.add(player);
    }

    @Subscribe
    public void onSessionLoaded(PlayerSessionLoadedEvent e){
        plugin.async(() -> {
            UUID playerUUID = e.getPlayerUUID();
            PlayerHandlerData playerHandlerData = VCorePaper.getInstance().getServices().getPipeline().load(PlayerHandlerData.class, playerUUID, Pipeline.LoadingStrategy.LOAD_PIPELINE,true);
            Player player = Bukkit.getPlayer(e.getPlayerUUID());
            if(player != null){
                if(playerHandlerData.restoreVanillaInventory)
                    playerHandlerData.restoreInventory("vanilla",() -> player);
                else
                    playerHandlerData.restoreInventory(() -> player);
                freezedPlayers.remove(player);
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerQuitEvent(PlayerQuitEvent e){
        PlayerHandlerData playerHandlerData = VCorePaper.getInstance().getServices().getPipeline().load(PlayerHandlerData.class, e.getPlayer().getUniqueId(), Pipeline.LoadingStrategy.LOAD_LOCAL,true);
        if(playerHandlerData.restoreVanillaInventory)
            playerHandlerData.setActiveInventoryID("vanilla");
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e){
        plugin.async(() -> {
            if(lastSync.containsKey(e.getPlayer().getUniqueId())){
                long timeStamp = lastSync.get(e.getPlayer().getUniqueId());
                if(System.currentTimeMillis() - timeStamp <= TimeUnit.SECONDS.toMillis(5))
                    return;
            }
            lastSync.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
            VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(e.getPlayer(), ChatMessageType.ACTION_BAR,"&eInventar synchronisiert");
            PlayerHandlerData playerHandlerData = VCorePaper.getInstance().getServices().getPipeline().load(PlayerHandlerData.class, e.getPlayer().getUniqueId(), Pipeline.LoadingStrategy.LOAD_PIPELINE,true);
            playerHandlerData.save(true);
        });
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e){
        plugin.async(() -> {
            if(lastSync.containsKey(e.getPlayer().getUniqueId())){
                long timeStamp = lastSync.get(e.getPlayer().getUniqueId());
                if(System.currentTimeMillis() - timeStamp <= TimeUnit.SECONDS.toMillis(5))
                    return;
            }
            lastSync.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
            VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(e.getPlayer(), ChatMessageType.ACTION_BAR,"&eInventar synchronisiert");
            PlayerHandlerData playerHandlerData = VCorePaper.getInstance().getServices().getPipeline().load(PlayerHandlerData.class, e.getPlayer().getUniqueId(), Pipeline.LoadingStrategy.LOAD_PIPELINE,true);
            playerHandlerData.save(true);
        });
    }

    @EventHandler
    public void dropItem(PlayerDropItemEvent e){
        Player player = e.getPlayer();
        if(!freezedPlayers.contains(player))
            return;
        e.setCancelled(true);
    }

    @EventHandler
    public void attemptPickupItem(PlayerAttemptPickupItemEvent e){
        Player player = e.getPlayer();
        if(!freezedPlayers.contains(player))
            return;
        e.setCancelled(true);
    }

    @EventHandler
    public void playerLeave(PlayerQuitEvent e){
        Player player = e.getPlayer();
        getPlugin().async(() -> {
            PlayerHandlerData playerHandlerData = VCorePaper.getInstance().getServices().getPipeline().load(PlayerHandlerData.class, player.getUniqueId(), Pipeline.LoadingStrategy.LOAD_PIPELINE,true);
            playerHandlerData.saveInventory(() -> player );
            playerHandlerData.save(true);
        });
        freezedPlayers.remove(player);
    }
}
