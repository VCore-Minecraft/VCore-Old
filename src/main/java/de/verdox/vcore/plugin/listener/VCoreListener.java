package de.verdox.vcore.plugin.listener;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import net.md_5.bungee.api.ProxyServer;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public abstract class VCoreListener <T extends VCorePlugin<?,?>>{

    protected final T plugin;

    public VCoreListener(VCoreSubsystem<T> subsystem){
        plugin = subsystem.getVCorePlugin();

        registerListener();
    }

    public VCoreListener(T plugin){
        this.plugin = plugin;
        registerListener();
    }

    protected abstract void registerListener();
    public T getPlugin() { return plugin; }

    public static class VCoreBukkitListener extends VCoreListener<VCorePlugin.Minecraft> implements Listener {

        public VCoreBukkitListener(VCoreSubsystem<VCorePlugin.Minecraft> subsystem) {
            super(subsystem);
        }

        public VCoreBukkitListener(VCorePlugin.Minecraft plugin) {
            super(plugin);
        }

        @Override
        protected void registerListener() {
            getPlugin().consoleMessage("&eRegistering Listener&7: &b"+getClass().getSimpleName(),false);
            Bukkit.getPluginManager().registerEvents(this,getPlugin().getPlugin());
        }
    }

    public static class VCoreBungeeListener extends VCoreListener<VCorePlugin.BungeeCord> implements net.md_5.bungee.api.plugin.Listener{

        public VCoreBungeeListener(VCoreSubsystem<VCorePlugin.BungeeCord> subsystem) {
            super(subsystem);
        }

        public VCoreBungeeListener(VCorePlugin.BungeeCord plugin) {
            super(plugin);
        }

        @Override
        protected void registerListener() {
            getPlugin().consoleMessage("&eRegistering Listener&7: &b"+getClass().getSimpleName(),false);
            ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
        }
    }
}
