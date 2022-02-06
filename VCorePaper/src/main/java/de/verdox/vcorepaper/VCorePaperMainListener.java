/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper;

import de.verdox.vcorepaper.impl.listener.VCorePaperListener;
import de.verdox.vcorepaper.impl.plugin.VCorePaperPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 26.08.2021 23:17
 */
public class VCorePaperMainListener extends VCorePaperListener {
    public VCorePaperMainListener(VCorePaperPlugin plugin) {
        super(plugin);
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
        e.leaveMessage(Component.text(""));
    }

}
