package de.verdox.vcorepaper.impl.plugin;

import de.verdox.vcore.plugin.PluginServiceParts;
import de.verdox.vcore.plugin.VCoreSubsystemManager;
import de.verdox.vcore.synchronization.pipeline.PipelineManager;
import de.verdox.vcore.synchronization.pipeline.player.PlayerDataManager;
import de.verdox.vcorepaper.impl.listener.VCorePaperPipelinePlayerListener;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.02.2022 20:51
 */
public class VCorePaperServiceParts extends PluginServiceParts<VCorePaperPlugin, VCorePaperSubsystem> {
    private VCoreSubsystemManager<VCorePaperPlugin, VCorePaperSubsystem> subsystemManager;
    private PlayerDataManager playerDataManager;

    public VCorePaperServiceParts(VCorePaperPlugin plugin) {
        super(plugin);
    }

    @Override
    public VCoreSubsystemManager<VCorePaperPlugin, VCorePaperSubsystem> getSubsystemManager() {
        return subsystemManager;
    }

    @Override
    public void enableAfter() {
        super.enableAfter();
        subsystemManager = new VCoreSubsystemManager<>(plugin);
        subsystemManager.enable();
        playerDataManager = new VCorePaperPipelinePlayerListener((PipelineManager) pipeline);
        this.pipeline.preloadAllData();
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
