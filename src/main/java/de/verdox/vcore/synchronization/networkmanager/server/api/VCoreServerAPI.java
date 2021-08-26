/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.server.api;

import de.verdox.vcore.synchronization.networkmanager.server.ServerInstance;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 04.08.2021 02:39
 */
public interface VCoreServerAPI {
    //TODO: G-list, G-TPS, SaveData für überall, Suggestionblocker, HelpBefehl der sich nach permissions aufbaut, alert Befehl
    void remoteShutdown(String serverName, boolean ignoreSelf);

    ServerInstance getServer(String serverName);
}
