package de.verdox.vcorepaper.custom.entities;

import de.verdox.vcore.event.listener.VCoreListener;
import de.verdox.vcore.plugin.VCorePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

public class CustomEntityListener extends VCoreListener.VCoreBukkitListener {
    public CustomEntityListener(VCorePlugin.Minecraft plugin) {
        super(plugin);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e){

    }
}
