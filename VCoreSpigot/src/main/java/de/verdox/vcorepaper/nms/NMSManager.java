/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms;

import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.nms.interfaces.server.NMSServerHandler;
import de.verdox.vcorepaper.nms.interfaces.world.NMSWorldHandler;
import org.bukkit.Bukkit;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 14.06.2021 14:39
 */
public class NMSManager {

    private final VCorePaper vCorePaper;
    private final NMSVersion nmsVersion;

    public NMSManager(VCorePaper vCorePaper){
        this.vCorePaper = vCorePaper;
        vCorePaper.consoleMessage("&eStarting NMS-Manager", false);
        String bukkitVersion = Bukkit.getBukkitVersion();
        nmsVersion = NMSVersion.findNMSVersion(bukkitVersion);
        if(nmsVersion != null)
            vCorePaper.consoleMessage("&eFound NMS-Version&7: &b"+nmsVersion.getNmsVersionTag(), false);
        else
            vCorePaper.consoleMessage("&cCould not find proper NMS Version for Version&7: &b"+bukkitVersion +" &8| &b"+Bukkit.getVersion(), false);
    }

    public NMSWorldHandler getNmsWorldHandler() {
        return NMSWorldHandler.getRightHandler(nmsVersion);
    }
    public NMSServerHandler getNMSServerHandler() {
        return NMSServerHandler.getRightHandler(nmsVersion);
    }
}
