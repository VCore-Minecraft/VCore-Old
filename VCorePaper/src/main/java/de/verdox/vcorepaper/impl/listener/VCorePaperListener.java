package de.verdox.vcorepaper.impl.listener;

import de.verdox.vcore.plugin.listener.VCoreListener;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import de.verdox.vcorepaper.impl.plugin.VCorePaperPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.02.2022 21:01
 */
public class VCorePaperListener extends VCoreListener<VCorePaperPlugin> implements Listener {

    public VCorePaperListener(@NotNull VCoreSubsystem<VCorePaperPlugin> subsystem) {
        super(subsystem);
    }

    public VCorePaperListener(@NotNull VCorePaperPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void registerListener() {
        getPlugin().consoleMessage("&eRegistering Listener&7: &b" + getClass().getSimpleName(), false);
        Bukkit.getPluginManager().registerEvents(this, getPlugin().getPlugin());
    }

}
