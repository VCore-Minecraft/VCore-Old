/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.player;

import de.verdox.vcore.plugin.SystemLoadable;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.messaging.messages.Message;
import de.verdox.vcore.synchronization.pipeline.PipelineManager;
import de.verdox.vcore.synchronization.pipeline.datatypes.PlayerData;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import de.verdox.vcore.synchronization.pipeline.player.events.PlayerPreSessionLoadEvent;
import de.verdox.vcore.synchronization.pipeline.player.events.PlayerPreSessionUnloadEvent;
import de.verdox.vcore.synchronization.pipeline.player.events.PlayerSessionLoadedEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Plugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 26.06.2021 00:33
 */
public class PlayerDataManager implements SystemLoadable {
    private final PipelineManager pipelineManager;
    protected final VCorePlugin<?,?> plugin;
    private boolean loaded;

    public PlayerDataManager(PipelineManager pipelineManager) {
        this.pipelineManager = pipelineManager;
        this.plugin = pipelineManager.getPlugin();
        loaded = true;
    }

    protected final void loginPipeline(@Nonnull UUID player){
        plugin.consoleMessage("&eHandling Player Join &b"+player,false);
        plugin.getServices().eventBus.post(new PlayerPreSessionLoadEvent(player));
        plugin.createTaskBatch().doAsync(() -> {
            plugin.getServices().getSubsystemManager().getActivePlayerDataClasses()
                    .forEach(aClass -> pipelineManager.load(aClass, player, Pipeline.LoadingStrategy.LOAD_PIPELINE,true));
        }).doSync(() -> plugin.getServices().eventBus.post(new PlayerSessionLoadedEvent(player, System.currentTimeMillis()))).executeBatch();
    }

    protected final void logoutPipeline(@Nonnull UUID player){
        plugin.getServices().eventBus.post(new PlayerPreSessionUnloadEvent(player));
        plugin.createTaskBatch()
                .doAsync(() -> {
                    plugin.getServices().getSubsystemManager().getActivePlayerDataClasses()
                            .forEach(aClass -> {
                                PlayerData data = pipelineManager.getLocalCache().getData(aClass, player);
                                data.save(true);
                            });
                }).executeBatch();
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void shutdown() {

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
            ProxyServer.getInstance().getPluginManager().registerListener((Plugin) pipelineManager.getPlugin(), this);
        }

        @net.md_5.bungee.event.EventHandler
        public void onJoin(PostLoginEvent e){
            loginPipeline(e.getPlayer().getUniqueId());
        }

        @net.md_5.bungee.event.EventHandler
        public void onPlayerLeave(PlayerDisconnectEvent e){
            logoutPipeline(e.getPlayer().getUniqueId());
        }
    }
}
