/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nms;

import de.verdox.vcore.modules.VCoreModule;
import de.verdox.vcore.nms.commands.NMSCommand;
import de.verdox.vcore.plugin.VCorePlugin;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 31.01.2022 19:24
 */
public class NMSModule implements VCoreModule<VCorePlugin.Minecraft> {

    private NMSManager nmsManager;
    private NMSCommand nmsCommand;

    @Override
    public void enableModule(VCorePlugin.Minecraft plugin) {
        nmsCommand = new NMSCommand(this, plugin, "nms");
        nmsManager = new NMSManager(plugin);
    }

    @Override
    public void disableModule() {

    }

    public NMSManager getNmsManager() {
        return nmsManager;
    }

    public NMSCommand getNmsCommand() {
        return nmsCommand;
    }
}
