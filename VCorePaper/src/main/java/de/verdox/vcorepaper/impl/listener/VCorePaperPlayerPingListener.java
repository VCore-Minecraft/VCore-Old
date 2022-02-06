package de.verdox.vcorepaper.impl.listener;

import de.verdox.vcore.synchronization.networkmanager.NetworkManager;
import de.verdox.vcore.synchronization.networkmanager.player.listener.VCorePlayerCacheListener;
import de.verdox.vcorepaper.impl.plugin.VCorePaperPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.02.2022 21:04
 */
public class VCorePaperPlayerPingListener extends VCorePaperListener implements VCorePlayerCacheListener {
    private final NetworkManager<?> networkManager;

    public VCorePaperPlayerPingListener(@NotNull NetworkManager<?> networkManager) {
        super((VCorePaperPlugin) networkManager.getPlugin());
        Objects.requireNonNull(networkManager, "networkManager can't be null!");
        this.networkManager = networkManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        plugin.async(() -> {
            sendPlayerPing(getPlugin(),
                    networkManager.getServerPingManager().getServerName(),
                    networkManager.getServerType(), PlayerPingType.JOIN,
                    e.getPlayer().getUniqueId(),
                    e.getPlayer().getName());
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        plugin.async(() -> {
            sendPlayerPing(getPlugin(),
                    networkManager.getServerPingManager().getServerName(),
                    networkManager.getServerType(), PlayerPingType.QUIT,
                    e.getPlayer().getUniqueId(),
                    e.getPlayer().getName());
        });
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        plugin.async(() -> {
            sendPlayerPing(getPlugin(),
                    networkManager.getServerPingManager().getServerName(),
                    networkManager.getServerType(), PlayerPingType.KICK,
                    e.getPlayer().getUniqueId(),
                    e.getPlayer().getName());
        });
    }
}
