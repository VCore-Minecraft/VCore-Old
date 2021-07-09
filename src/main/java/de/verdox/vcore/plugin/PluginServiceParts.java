/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin;

import com.google.common.eventbus.EventBus;
import de.verdox.vcore.performance.concurrent.VCoreScheduler;
import de.verdox.vcore.plugin.files.DebugConfig;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import de.verdox.vcore.synchronization.messaging.MessagingConfig;
import de.verdox.vcore.synchronization.messaging.MessagingService;
import de.verdox.vcore.synchronization.pipeline.PipelineConfig;
import de.verdox.vcore.synchronization.pipeline.PipelineManager;
import de.verdox.vcore.synchronization.pipeline.player.PlayerDataManager;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 29.06.2021 23:21
 */
public abstract class PluginServiceParts <T extends VCorePlugin<?,S>, S extends VCoreSubsystem<T>> implements SystemLoadable {
    protected final T plugin;
    protected final PipelineConfig pipelineConfig;
    protected final MessagingConfig messagingConfig;
    private final DebugConfig debugConfig;

    protected VCoreScheduler vCoreScheduler;
    public final EventBus eventBus;
    protected Pipeline pipeline;
    protected MessagingService<?> messagingService;

    protected boolean loaded;

    PluginServiceParts(T plugin){
        this.plugin = plugin;
        debugConfig = new DebugConfig(plugin);
        debugConfig.init();
        this.eventBus = new EventBus();
        this.pipelineConfig = new PipelineConfig(plugin, "PipelineSettings.yml","//pipeline");
        this.pipelineConfig.init();
        this.messagingConfig = new MessagingConfig(plugin,"messagingConfig.yml","//messaging");
        this.messagingConfig.init();
    }

    public final DebugConfig getDebugConfig() {
        return debugConfig;
    }

    public void enableBefore(){
        this.vCoreScheduler = new VCoreScheduler(plugin);
        this.pipeline = pipelineConfig.constructPipeline(plugin);
        this.messagingService = messagingConfig.constructMessagingService();

    }

    public void enableAfter(){

    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void shutdown() {
        plugin.consoleMessage("&6Shutting down VCore Parts",false);
        pipeline.saveAllData();
        vCoreScheduler.waitUntilShutdown();

        pipeline.shutdown();
        messagingService.shutdown();
        getSubsystemManager().shutdown();
        getPlayerDataManager().shutdown();
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public VCoreScheduler getVCoreScheduler() {
        return vCoreScheduler;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public MessagingService<?> getMessagingService() {
        return messagingService;
    }

    public abstract VCoreSubsystemManager<T,S> getSubsystemManager();
    public abstract PlayerDataManager getPlayerDataManager();

    public static class Bukkit extends PluginServiceParts<VCorePlugin.Minecraft,VCoreSubsystem.Bukkit>{

        private VCoreSubsystemManager<VCorePlugin.Minecraft,VCoreSubsystem.Bukkit> subsystemManager;
        private PlayerDataManager playerDataManager;

        Bukkit(VCorePlugin.Minecraft plugin) {
            super(plugin);
        }

        @Override
        public VCoreSubsystemManager<VCorePlugin.Minecraft, VCoreSubsystem.Bukkit> getSubsystemManager() {
            return subsystemManager;
        }

        @Override
        public void enableAfter() {
            super.enableAfter();
            subsystemManager = new VCoreSubsystemManager<>(plugin);
            subsystemManager.enable();
            playerDataManager = new PlayerDataManager.Bukkit((PipelineManager) pipeline);

            loaded = true;
        }

        @Override
        public final void shutdown() {

            super.shutdown();
            subsystemManager.shutdown();
        }

        @Override
        public PlayerDataManager getPlayerDataManager() {
            return playerDataManager;
        }
    }

    public static class BungeeCord extends PluginServiceParts<VCorePlugin.BungeeCord,VCoreSubsystem.BungeeCord>{

        private VCoreSubsystemManager<VCorePlugin.BungeeCord,VCoreSubsystem.BungeeCord> subsystemManager;
        private PlayerDataManager playerDataManager;

        BungeeCord(VCorePlugin.BungeeCord plugin) {
            super(plugin);
        }

        @Override
        public void enableAfter() {
            subsystemManager = new VCoreSubsystemManager<>(plugin);
            subsystemManager.enable();
            playerDataManager = new PlayerDataManager.BungeeCord((PipelineManager) pipeline);
            loaded = true;

        }

        @Override
        public final void shutdown() {

            ProxyServer.getInstance().getPlayers().forEach(Connection::disconnect);
            super.shutdown();
            subsystemManager.shutdown();
        }

        @Override
        public VCoreSubsystemManager<VCorePlugin.BungeeCord, VCoreSubsystem.BungeeCord> getSubsystemManager() {
            return subsystemManager;
        }

        @Override
        public PlayerDataManager getPlayerDataManager() {
            return playerDataManager;
        }
    }
}
