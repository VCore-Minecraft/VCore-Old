package de.verdox.vcorepaper.impl.listener;

import de.verdox.vcore.synchronization.pipeline.PipelineManager;
import de.verdox.vcore.synchronization.pipeline.player.PlayerDataManager;
import de.verdox.vcorepaper.impl.plugin.VCorePaperPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.02.2022 21:56
 */
public class VCorePaperPipelinePlayerListener extends PlayerDataManager implements Listener {
    public VCorePaperPipelinePlayerListener(@NotNull PipelineManager pipelineManager) {
        super(pipelineManager);
        VCorePaperPlugin vCorePaperPlugin = (VCorePaperPlugin) plugin;
        vCorePaperPlugin.getPlugin().getServer().getPluginManager().registerEvents(this, vCorePaperPlugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        loginPipeline(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeave(PlayerQuitEvent e) {
        logoutPipeline(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKick(PlayerKickEvent e) {
        logoutPipeline(e.getPlayer().getUniqueId());
    }
}
