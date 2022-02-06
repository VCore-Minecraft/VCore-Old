package de.verdox.vcorewaterfall.impl.plugin;

import de.verdox.vcore.plugin.PluginServiceParts;
import de.verdox.vcore.plugin.VCoreSubsystemManager;
import de.verdox.vcore.synchronization.pipeline.PipelineManager;
import de.verdox.vcore.synchronization.pipeline.player.PlayerDataManager;
import de.verdox.vcorewaterfall.impl.listener.VCoreWaterfallPlayerPipelineListener;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.02.2022 22:36
 */
public class VCoreWaterfallServiceParts extends PluginServiceParts<VCoreWaterfallPlugin, VCoreWaterfallSubsystem> {

    private VCoreSubsystemManager<VCoreWaterfallPlugin, VCoreWaterfallSubsystem> subsystemManager;
    private PlayerDataManager playerDataManager;

    VCoreWaterfallServiceParts(VCoreWaterfallPlugin plugin) {
        super(plugin);
    }

    @Override
    public void enableAfter() {
        subsystemManager = new VCoreSubsystemManager<>(plugin);
        subsystemManager.enable();
        playerDataManager = new VCoreWaterfallPlayerPipelineListener((PipelineManager) pipeline);
        this.pipeline.preloadAllData();
        loaded = true;

    }

    @Override
    public final void shutdown() {

        ProxyServer.getInstance().getPlayers().forEach(Connection::disconnect);
        super.shutdown();
        subsystemManager.shutdown();
    }

    @Override
    public VCoreSubsystemManager<VCoreWaterfallPlugin, VCoreWaterfallSubsystem> getSubsystemManager() {
        return subsystemManager;
    }

    @Override
    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
}
