/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.pipeline.datatypes.PlayerData;
import de.verdox.vcore.synchronization.pipeline.player.events.PlayerPreSessionLoadEvent;
import de.verdox.vcore.synchronization.pipeline.player.events.PlayerPreSessionUnloadEvent;
import de.verdox.vcore.synchronization.pipeline.player.events.PlayerSessionLoadedEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 26.06.2021 00:33
 */
public class PlayerDataManager {
    private final PipelineManager pipelineManager;
    protected final VCorePlugin<?,?> plugin;

    public PlayerDataManager(PipelineManager pipelineManager) {
        this.pipelineManager = pipelineManager;
        this.plugin = pipelineManager.getPlugin();
    }

    protected final void loginPipeline(UUID player){
        plugin.getEventBus().post(new PlayerPreSessionLoadEvent(player));
        plugin.createTaskBatch().doAsync(() -> {
            plugin.getSubsystemManager().getActivePlayerDataClasses()
                    .forEach(aClass -> {
                        pipelineManager.loadFromPipeline(aClass, player, true);
                    });
        }).doSync(() -> {
            plugin.getEventBus().post(new PlayerSessionLoadedEvent(player, System.currentTimeMillis()));
        }).executeBatch();
    }

    protected final void logoutPipeline(UUID player){
        plugin.getEventBus().post(new PlayerPreSessionUnloadEvent(player));
        plugin.createTaskBatch().doAsync(() -> {
            plugin.getSubsystemManager().getActivePlayerDataClasses()
                    .stream()
                    .distinct()
                    .forEach(aClass -> {
                        if(!pipelineManager.getLocalCache().dataExist(aClass, player))
                            return;
                        PlayerData playerData = plugin.getDataPipeline().getLocalCache().getData(aClass, player);
                        playerData.save(true,false);
                        playerData.cleanUp();
                        pipelineManager.getLocalCache().remove(aClass, player);
                    });
        }).executeBatch();
    }

    public static class Bukkit extends PlayerDataManager implements Listener {

        public Bukkit(PipelineManager pipelineManager) {
            super(pipelineManager);
            VCorePlugin.Minecraft bukkitPlugin = (VCorePlugin.Minecraft) plugin;
            bukkitPlugin.getPlugin().getServer().getPluginManager().registerEvents(this,bukkitPlugin);
        }

        @EventHandler
        public void onJoin(PlayerJoinEvent e){
            loginPipeline(e.getPlayer().getUniqueId());
        }

        @EventHandler
        public void onLeave(PlayerQuitEvent e){
            logoutPipeline(e.getPlayer().getUniqueId());
        }

        @EventHandler
        public void onKick(PlayerKickEvent e){
            logoutPipeline(e.getPlayer().getUniqueId());
        }

    }

    public static class BungeeCord extends PlayerDataManager implements net.md_5.bungee.api.plugin.Listener {
        public BungeeCord(PipelineManager pipelineManager) {
            super(pipelineManager);
        }

        @net.md_5.bungee.event.EventHandler
        public void onJoin(PostLoginEvent e){
            loginPipeline(e.getPlayer().getUniqueId());
        }

        @net.md_5.bungee.event.EventHandler
        public void onJoin(PlayerDisconnectEvent e){
            logoutPipeline(e.getPlayer().getUniqueId());
        }
    }
}
