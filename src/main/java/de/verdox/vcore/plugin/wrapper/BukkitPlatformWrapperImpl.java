/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.wrapper;

import de.verdox.vcore.plugin.VCorePlugin;
import org.bukkit.Bukkit;

import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 01.08.2021 20:01
 */
public class BukkitPlatformWrapperImpl implements PlatformWrapper<VCorePlugin.Minecraft> {
    @Override
    public boolean isPlayerOnline(UUID playerUUID) {
        return Bukkit.getPlayer(playerUUID) != null;
    }

    @Override
    public boolean isPrimaryThread() {
        return Bukkit.isPrimaryThread();
    }
}
