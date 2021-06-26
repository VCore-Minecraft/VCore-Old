/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.bukkitplayerhandler.listener;

import com.google.common.eventbus.Subscribe;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import de.verdox.vcore.synchronization.pipeline.player.events.PlayerPreSessionUnloadEvent;
import de.verdox.vcore.synchronization.pipeline.player.events.PlayerSessionLoadedEvent;
import de.verdox.vcore.plugin.listener.VCoreListener;
import de.verdox.vcore.plugin.player.VCorePlayer;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.bukkitplayerhandler.playerdata.PlayerHandlerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

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
        UUID playerUUID = e.getPlayerUUID();
        PlayerHandlerData playerHandlerData = VCorePaper.getInstance().getDataPipeline().load(PlayerHandlerData.class, playerUUID, Pipeline.LoadingStrategy.LOAD_PIPELINE,true);
        if(playerHandlerData.restoreVanillaInventory){
            playerHandlerData.restoreInventory();
        }
    }

    @Subscribe
    public void onSessionPreUnload(PlayerPreSessionUnloadEvent playerPreSessionUnloadEvent){
        Player player = Bukkit.getPlayer(playerPreSessionUnloadEvent.getPlayerUUID());
        PlayerHandlerData playerHandlerData = VCorePaper.getInstance().getDataPipeline().load(PlayerHandlerData.class, player.getUniqueId(), Pipeline.LoadingStrategy.LOAD_PIPELINE,true);
        playerHandlerData.saveInventory(() -> player);
    }

}
