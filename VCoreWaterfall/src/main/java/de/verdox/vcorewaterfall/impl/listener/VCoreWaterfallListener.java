package de.verdox.vcorewaterfall.impl.listener;

import de.verdox.vcore.plugin.listener.VCoreListener;
import de.verdox.vcorewaterfall.impl.plugin.VCoreWaterfallPlugin;
import de.verdox.vcorewaterfall.impl.plugin.VCoreWaterfallSubsystem;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.02.2022 22:55
 */
public class VCoreWaterfallListener extends VCoreListener<VCoreWaterfallPlugin> implements Listener {
    public VCoreWaterfallListener(@NotNull VCoreWaterfallSubsystem subsystem) {
        super(subsystem);
    }

    public VCoreWaterfallListener(@NotNull VCoreWaterfallPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void registerListener() {
        getPlugin().consoleMessage("&eRegistering Listener&7: &b" + getClass().getSimpleName(), false);
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }
}
