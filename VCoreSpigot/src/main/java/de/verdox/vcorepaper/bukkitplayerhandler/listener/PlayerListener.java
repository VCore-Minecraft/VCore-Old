/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.bukkitplayerhandler.listener;

import com.google.common.eventbus.Subscribe;
import de.verdox.vcore.data.events.PlayerPreSessionUnloadEvent;
import de.verdox.vcore.data.events.PlayerSessionLoadedEvent;
import de.verdox.vcore.data.manager.LoadingStrategy;
import de.verdox.vcore.data.session.PlayerSession;
import de.verdox.vcore.event.listener.VCoreListener;
import de.verdox.vcore.player.VCorePlayer;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.bukkitplayerhandler.playerdata.PlayerHandlerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 20.06.2021 00:46
 */
public class PlayerListener extends VCoreListener.VCoreBukkitListener {
    public PlayerListener(VCoreSubsystem<VCorePlugin.Minecraft> subsystem) {
        super(subsystem);
    }

    @Subscribe
    public void onSessionLoaded(PlayerSessionLoadedEvent e){
        PlayerSession playerSession = e.getPlayerSession();
        PlayerHandlerData playerHandlerData = playerSession.getDataManager().load(PlayerHandlerData.class, playerSession.getUuid(), LoadingStrategy.LOAD_PIPELINE,true);
        if(playerHandlerData.restoreVanillaInventory){
            playerHandlerData.restoreInventory();
        }
    }

    @Subscribe
    public void onSessionPreUnload(PlayerPreSessionUnloadEvent playerPreSessionUnloadEvent){
        VCorePlayer vCorePlayer = VCorePaper.getInstance().getVCorePlayerManager().getPlayer(playerPreSessionUnloadEvent.getPlayerUUID());
        Player player = vCorePlayer.toBukkitPlayer();
        VCorePaper.getInstance().async(() -> {
            PlayerHandlerData playerHandlerData = VCorePaper.getInstance().getSessionManager().load(PlayerHandlerData.class, vCorePlayer.getPlayerUUID(), LoadingStrategy.LOAD_PIPELINE,true);
            playerHandlerData.saveInventory(() -> player);
        });
    }

}
