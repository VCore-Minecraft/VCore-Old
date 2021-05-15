package de.verdox.vcore.event.listener;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;
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
}
