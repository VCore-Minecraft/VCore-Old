package de.verdox.vcorewaterfall.impl.listener;

import de.verdox.vcore.synchronization.pipeline.PipelineManager;
import de.verdox.vcore.synchronization.pipeline.player.PlayerDataManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.02.2022 22:58
 */
public class VCoreWaterfallPlayerPipelineListener extends PlayerDataManager implements Listener {
    public VCoreWaterfallPlayerPipelineListener(@NotNull PipelineManager pipelineManager) {
        super(pipelineManager);
        ProxyServer.getInstance().getPluginManager().registerListener((Plugin) pipelineManager.getPlugin(), this);
    }

    @net.md_5.bungee.event.EventHandler(priority = 5)
    public void onJoin(PostLoginEvent e) {
        loginPipeline(e.getPlayer().getUniqueId());
    }

    @net.md_5.bungee.event.EventHandler(priority = 5)
    public void onPlayerLeave(PlayerDisconnectEvent e) {
        logoutPipeline(e.getPlayer().getUniqueId());
    }
}
