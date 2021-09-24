/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.workernpc.listener;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.listener.VCoreListener;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.workernpc.WorkerNPC;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 22.09.2021 13:06
 */
public class WorkerNPCListener extends VCoreListener.VCoreBukkitListener {
    public WorkerNPCListener(VCorePlugin.Minecraft plugin) {
        super(plugin);
    }

    @EventHandler
    public void playerInteract(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        Entity entity = e.getRightClicked();
        if (!entity.getType().equals(EntityType.VILLAGER))
            return;
        WorkerNPC workerNPC = VCorePaper.getInstance().getCustomEntityManager().wrap(WorkerNPC.class, entity);
        e.setCancelled(true);
        plugin.async(() -> {
            workerNPC.openDialog(player);
            player.playSound(e.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 0.4f, 1f);
        });
        workerNPC.getDataHolder().setRotation(player.getLocation().getYaw() - 180, workerNPC.getDataHolder().getLocation().getPitch());
    }
}
