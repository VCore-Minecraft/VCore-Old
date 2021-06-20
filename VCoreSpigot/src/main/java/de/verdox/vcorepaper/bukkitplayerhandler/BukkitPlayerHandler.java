/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.bukkitplayerhandler;

import de.verdox.vcore.data.datatypes.PlayerData;
import de.verdox.vcore.data.datatypes.ServerData;
import de.verdox.vcore.dataconnection.mongodb.annotation.MongoDBIdentifier;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import de.verdox.vcorepaper.bukkitplayerhandler.listener.PlayerListener;
import de.verdox.vcorepaper.bukkitplayerhandler.playerdata.PlayerHandlerData;

import java.util.List;
import java.util.Set;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 20.06.2021 00:19
 */
@MongoDBIdentifier(identifier = "BukkitPlayerHandler")
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
        getVCorePlugin().getEventBus().register(new PlayerListener(this));
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
