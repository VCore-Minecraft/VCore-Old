/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin;

import de.verdox.vcore.modules.VCoreModule;
import de.verdox.vcore.modules.VCoreModuleLoader;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import de.verdox.vcore.synchronization.networkmanager.NetworkManager;
import de.verdox.vcore.synchronization.networkmanager.player.api.VCorePlayerAPI;
import de.verdox.vcore.synchronization.networkmanager.server.api.VCoreServerAPI;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 03.08.2021 20:28
 */
public interface VCoreCoreInstance<T, R extends VCoreSubsystem<?>> extends VCorePlugin<T, R> {
    VCorePlayerAPI getPlayerAPI();

    VCoreServerAPI getServerAPI();

    <X extends VCorePlugin<T, R>> NetworkManager<X> getNetworkManager();

    String getServerName();

    VCoreModuleLoader<T, R, ? extends VCoreCoreInstance<T, R>, ? extends VCoreModule<T, R, ? extends VCoreCoreInstance<T, R>>> getModuleLoader();
}
