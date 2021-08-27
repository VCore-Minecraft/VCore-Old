/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper;

import com.google.common.eventbus.Subscribe;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.listener.VCoreListener;
import de.verdox.vcore.synchronization.networkmanager.serverping.events.ServerPingOfflineEvent;
import de.verdox.vcore.synchronization.networkmanager.serverping.events.ServerPingOnlineEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 26.08.2021 23:17
 */
public class VCorePaperMainListener extends VCoreListener.VCoreBukkitListener {
    public VCorePaperMainListener(VCorePlugin.Minecraft plugin) {
        super(plugin);
    }

    @Subscribe
    public void onServerOnline(ServerPingOnlineEvent e) {
        if (!e.isFirstReceivedPing())
            return;

    }

    @Subscribe
    public void onServerOffline(ServerPingOfflineEvent e) {

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.joinMessage(null);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.quitMessage(null);
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        e.leaveMessage(null);
    }

}
