/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.workernpc.listener;

import de.verdox.vcore.workernpc.WorkerNPC;
import de.verdox.vcore.workernpc.VCoreWorkerNPCModule;
import de.verdox.vcorepaper.impl.listener.VCorePaperListener;
import de.verdox.vcorepaper.impl.plugin.VCorePaperPlugin;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 22.09.2021 13:06
 */
public class WorkerNPCListener extends VCorePaperListener {
    private final VCoreWorkerNPCModule workerNPCModule;

    public WorkerNPCListener(VCorePaperPlugin plugin, VCoreWorkerNPCModule workerNPCModule) {
        super(plugin);
        this.workerNPCModule = workerNPCModule;
    }

    @EventHandler
    public void playerInteract(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        Entity entity = e.getRightClicked();
        if (!entity.getType().equals(EntityType.VILLAGER))
            return;
        WorkerNPC workerNPC = workerNPCModule.toWorkerNPC((LivingEntity) entity);
        if (!workerNPC.verify())
            return;
        e.setCancelled(true);
        plugin.async(() -> {
            workerNPC.openDialog(player);
            player.playSound(e.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 0.4f, 1f);
        });
        workerNPC.getDataHolder().setRotation(player.getLocation().getYaw() - 180, workerNPC.getDataHolder().getLocation().getPitch());
    }
}
