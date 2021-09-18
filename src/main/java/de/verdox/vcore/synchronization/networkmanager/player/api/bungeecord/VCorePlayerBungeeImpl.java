/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.api.bungeecord;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.networkmanager.player.api.VCorePlayerAPIImpl;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 03.08.2021 23:23
 */
public class VCorePlayerBungeeImpl extends VCorePlayerAPIImpl implements Listener {
    public VCorePlayerBungeeImpl(VCorePlugin.BungeeCord plugin) {
        super(plugin);
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onJoin(LoginEvent e) {
        CompletableFuture<Set<Runnable>> future = vCorePlayerTaskScheduler.getAllTasks(e.getConnection().getUniqueId());
        plugin.async(() -> {
            try {
                if (ProxyServer.getInstance().getPlayer(e.getConnection().getUniqueId()) == null)
                    return;
                Set<Runnable> tasks = future.get(5, TimeUnit.SECONDS);
                plugin.consoleMessage("&eFound Tasks for &e" + e.getConnection().getName() + " &b" + tasks.size(), false);
                if (tasks.size() == 0)
                    return;
                plugin.consoleMessage("&eExecuting pending tasks for player &e" + e.getConnection().getName(), false);
                tasks.forEach(plugin::sync);
            } catch (InterruptedException | ExecutionException | TimeoutException interruptedException) {
                interruptedException.printStackTrace();
            }
        });
    }
}
