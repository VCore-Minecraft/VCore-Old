/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.bukkitplayerhandler;

import de.verdox.vcore.synchronization.pipeline.annotations.DataStorageIdentifier;
import de.verdox.vcore.synchronization.pipeline.datatypes.PlayerData;
import de.verdox.vcore.synchronization.pipeline.datatypes.ServerData;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import de.verdox.vcorepaper.bukkitplayerhandler.command.PlayerHandlerAdminCommands;
import de.verdox.vcorepaper.bukkitplayerhandler.listener.PlayerListener;
import de.verdox.vcorepaper.bukkitplayerhandler.papi.BukkitPlayerHandlerExpansion;
import de.verdox.vcorepaper.bukkitplayerhandler.playerdata.PlayerHandlerData;
import org.bukkit.Bukkit;

import java.util.Set;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 20.06.2021 00:19
 */
@DataStorageIdentifier(identifier = "BukkitPlayerHandler")
public class BukkitPlayerHandler extends VCoreSubsystem.Bukkit {
    public BukkitPlayerHandler(VCorePlugin.Minecraft VCorePlugin) {
        super(VCorePlugin);
    }

    @Override
    public boolean isActivated() {
        return true;
    }

    @Override
    public void onSubsystemEnable() {
        getVCorePlugin().getServices().eventBus.register(new PlayerListener(this));
        if(org.bukkit.Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            new BukkitPlayerHandlerExpansion().register();
        new PlayerHandlerAdminCommands(this,"playerhandler");
    }

    @Override
    public void onSubsystemDisable() {

    }

    @Override
    public Set<Class<? extends PlayerData>> playerDataClasses() {
        return Set.of(PlayerHandlerData.class);
    }

    @Override
    public Set<Class<? extends ServerData>> serverDataClasses() {
        return null;
    }
}
