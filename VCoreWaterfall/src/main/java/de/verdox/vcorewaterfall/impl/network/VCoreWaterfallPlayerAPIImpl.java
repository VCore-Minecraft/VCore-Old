package de.verdox.vcorewaterfall.impl.network;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.networkmanager.player.api.VCorePlayerAPIImpl;
import de.verdox.vcorewaterfall.impl.plugin.VCoreWaterfallPlugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.02.2022 22:44
 */
public class VCoreWaterfallPlayerAPIImpl extends VCorePlayerAPIImpl implements Listener {
    public VCoreWaterfallPlayerAPIImpl(@NotNull VCoreWaterfallPlugin plugin) {
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