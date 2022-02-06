/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nbt.entities;

import de.verdox.vcore.nbt.VCoreNBTModule;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.listener.VCoreListener;
import de.verdox.vcorepaper.impl.listener.VCorePaperListener;
import de.verdox.vcorepaper.impl.plugin.VCorePaperPlugin;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.Objects;

public class CustomEntityListener extends VCorePaperListener {
    private final VCoreNBTModule vCoreNBTModule;

    public CustomEntityListener(VCoreNBTModule vCoreNBTModule, VCorePaperPlugin plugin) {
        super(plugin);
        this.vCoreNBTModule = vCoreNBTModule;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {

    }

    @EventHandler(ignoreCancelled = true)
    public void playerInteract(PlayerInteractAtEntityEvent e) {
        Entity entity = e.getRightClicked();
        VCoreEntity vCoreEntity = vCoreNBTModule.getCustomEntityManager().wrap(VCoreEntity.class, entity);

        vCoreEntity.getCustomDataKeys()
                .stream()
                .map(key -> vCoreNBTModule.getCustomEntityManager().getDataType(key))
                .filter(Objects::nonNull)
                .forEach(entityCustomData -> entityCustomData.onPlayerRightClick(e, vCoreEntity));
    }
}
