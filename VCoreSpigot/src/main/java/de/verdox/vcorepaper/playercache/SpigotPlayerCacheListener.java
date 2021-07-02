/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.playercache;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.listener.VCoreListener;
import de.verdox.vcore.synchronization.messaging.messages.Message;
import de.verdox.vcore.synchronization.pipeline.player.VCorePlayerCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 02.07.2021 17:22
 */
public class SpigotPlayerCacheListener extends VCoreListener.VCoreBukkitListener {

    public SpigotPlayerCacheListener(VCorePlugin.Minecraft plugin) {
        super(plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        plugin.async(() -> {
            Message message = plugin.getServices().getMessagingService().constructMessage()
                    .withParameters("connection", "minecraft", "join")
                    .withData(e.getPlayer().getUniqueId(), e.getPlayer().getName())
                    .constructMessage();
            plugin.getServices().getMessagingService().publishMessage(message);
        });
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        plugin.async(() -> {
            Message message = plugin.getServices().getMessagingService().constructMessage()
                    .withParameters("connection", "minecraft", "leave")
                    .withData(e.getPlayer().getUniqueId(), e.getPlayer().getName())
                    .constructMessage();
            plugin.getServices().getMessagingService().publishMessage(message);
        });
    }

    @EventHandler
    public void onKick(PlayerKickEvent e){
        plugin.async(() -> {
            Message message = plugin.getServices().getMessagingService().constructMessage()
                    .withParameters("connection", "minecraft", "kick")
                    .withData(e.getPlayer().getUniqueId(), e.getPlayer().getName())
                    .constructMessage();
            plugin.getServices().getMessagingService().publishMessage(message);
        });
    }

}
