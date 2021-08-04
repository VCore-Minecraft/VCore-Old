/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.wrapper;

import de.verdox.vcore.plugin.VCorePlugin;
import net.md_5.bungee.api.ProxyServer;

import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 01.08.2021 20:01
 */
public class BungeePlatformWrapperImpl implements PlatformWrapper<VCorePlugin.BungeeCord>{
    @Override
    public boolean isPlayerOnline(UUID playerUUID) {
        return ProxyServer.getInstance().getPlayer(playerUUID) != null;
    }

    @Override
    public boolean isPrimaryThread() {
        return false;
    }
}
