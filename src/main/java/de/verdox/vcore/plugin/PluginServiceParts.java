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
import de.verdox.vcore.synchronization.networkmanager.server.ServerInstance;
import de.verdox.vcore.synchronization.pipeline.PipelineConfig;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import de.verdox.vcore.synchronization.pipeline.player.PlayerDataManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 29.06.2021 23:21
 */
public abstract class PluginServiceParts<T extends VCorePlugin<?, S>, S extends VCoreSubsystem<T>> implements SystemLoadable {

    public final EventBus eventBus;
    protected final T plugin;
    protected final PipelineConfig pipelineConfig;
    protected final MessagingConfig messagingConfig;
    private final DebugConfig debugConfig;
    protected VCoreScheduler vCoreScheduler;
    protected Pipeline pipeline;
    protected MessagingService<?> messagingService;

    protected boolean loaded;

    public PluginServiceParts(@NotNull T plugin) {
        Objects.requireNonNull(plugin, "plugin can't be null!");
        this.plugin = plugin;
        debugConfig = new DebugConfig(plugin);
        debugConfig.init();
        this.eventBus = new EventBus();
        this.pipelineConfig = new PipelineConfig(plugin, "PipelineSettings.yml", "//pipeline");
        this.pipelineConfig.init();
        this.messagingConfig = new MessagingConfig(plugin, "messagingConfig.yml", "//messaging");
        this.messagingConfig.init();
    }

    public final DebugConfig getDebugConfig() {
        return debugConfig;
    }

    public void enableBefore() {
        this.vCoreScheduler = new VCoreScheduler(plugin);
        this.pipeline = pipelineConfig.constructPipeline(plugin);
        this.messagingService = messagingConfig.constructMessagingService();
    }

    public ServerInstance getServerInstance() {
        return getPipeline().load(ServerInstance.class, plugin.getCoreInstance().getNetworkManager().getServerCache().getServerUUID(plugin.getCoreInstance().getServerName()), Pipeline.LoadingStrategy.LOAD_PIPELINE);
    }

    public void enableAfter() {
        this.messagingService.setupPrivateMessagingChannel();
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void shutdown() {
        plugin.consoleMessage("&6Shutting down VCore Parts", false);
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

    public abstract VCoreSubsystemManager<T, S> getSubsystemManager();

    public abstract PlayerDataManager getPlayerDataManager();
}
