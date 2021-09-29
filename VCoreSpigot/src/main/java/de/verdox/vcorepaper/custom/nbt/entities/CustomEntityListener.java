/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbt.entities;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.listener.VCoreListener;
import de.verdox.vcorepaper.VCorePaper;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.Objects;

public class CustomEntityListener extends VCoreListener.VCoreBukkitListener {
    public CustomEntityListener(VCorePlugin.Minecraft plugin) {
        super(plugin);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {

    }

    @EventHandler(ignoreCancelled = true)
    public void playerInteract(PlayerInteractAtEntityEvent e) {
        Entity entity = e.getRightClicked();
        VCoreEntity vCoreEntity = VCorePaper.getInstance().getCustomEntityManager().wrap(VCoreEntity.class, entity);

        vCoreEntity.getCustomDataKeys()
                .stream()
                .map(key -> VCorePaper.getInstance().getCustomEntityManager().getDataType(key))
                .filter(Objects::nonNull)
                .forEach(entityCustomData -> entityCustomData.onPlayerRightClick(e, vCoreEntity));
    }
}
