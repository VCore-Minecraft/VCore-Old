/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.talkingnpc;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.listener.VCoreListener;
import de.verdox.vcorepaper.VCorePaper;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 13.09.2021 23:08
 */
public class TalkingNPCListener extends VCoreListener.VCoreBukkitListener {
    public TalkingNPCListener(VCorePlugin.Minecraft plugin) {
        super(plugin);
    }

    @EventHandler
    public void playerInteract(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        Entity entity = e.getRightClicked();
        if (!entity.getType().equals(EntityType.VILLAGER))
            return;
        TalkingNPC talkingNPC = VCorePaper.getInstance().getCustomEntityManager().getTalkingNPCService().getTalkingNPC(entity);
        e.setCancelled(true);
        talkingNPC.openDialogGUI(player);
        player.playSound(e.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 0.4f, 1f);
    }
}
