/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nms;

import de.verdox.vcore.nms.nmshandler.api.entity.NMSEntityHandler;
import de.verdox.vcore.nms.nmshandler.api.player.NMSPlayerHandler;
import de.verdox.vcore.nms.nmshandler.api.server.NMSServerHandler;
import de.verdox.vcore.nms.nmshandler.api.world.NMSWorldHandler;
import de.verdox.vcore.plugin.VCorePlugin;
import org.bukkit.Bukkit;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 14.06.2021 14:39
 */
public class NMSManager {

    private final VCorePlugin.Minecraft vCorePlugin;
    private final NMSVersion nmsVersion;

    public NMSManager(VCorePlugin.Minecraft vCorePlugin) {
        this.vCorePlugin = vCorePlugin;
        vCorePlugin.consoleMessage("&eStarting NMS-Manager", false);
        String bukkitVersion = Bukkit.getBukkitVersion();
        nmsVersion = NMSVersion.findNMSVersion(bukkitVersion);
        if (nmsVersion != null)
            vCorePlugin.consoleMessage("&eFound NMS-Version&7: &b" + nmsVersion.getNmsVersionTag(), false);
        else
            vCorePlugin.consoleMessage("&cCould not find proper NMS Version for Version&7: &b" + bukkitVersion + " &8| &b" + Bukkit.getVersion(), false);
    }

    public NMSWorldHandler getNmsWorldHandler() {
        return NMSWorldHandler.getRightHandler(nmsVersion);
    }

    public NMSServerHandler getNMSServerHandler() {
        return NMSServerHandler.getRightHandler(nmsVersion);
    }

    public NMSEntityHandler getNMSEntityHandler() {
        return NMSEntityHandler.getRightHandler(nmsVersion);
    }

    public NMSPlayerHandler getNMSPlayerHandler() {
        return NMSPlayerHandler.getRightHandler(nmsVersion);
    }
}