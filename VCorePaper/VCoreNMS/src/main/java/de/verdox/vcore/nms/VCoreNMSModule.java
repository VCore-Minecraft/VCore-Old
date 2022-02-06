/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nms;

import de.verdox.vcore.modules.VCoreModule;
import de.verdox.vcore.nms.api.commands.NMSCommand;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.module.VCorePaperModule;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 31.01.2022 19:24
 */
public class VCoreNMSModule extends VCorePaperModule {

    private de.verdox.vcore.nms.NMSManager nmsManager;
    private NMSCommand nmsCommand;

    @Override
    public void enableModule(VCorePaper coreInstance) {
        nmsCommand = new NMSCommand(this, this, "nms");
        nmsManager = new de.verdox.vcore.nms.NMSManager(coreInstance);
    }

    @Override
    public void disableModule() {

    }

    public de.verdox.vcore.nms.NMSManager getNmsManager() {
        return nmsManager;
    }

    public NMSCommand getNmsCommand() {
        return nmsCommand;
    }
}
