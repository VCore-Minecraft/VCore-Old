package de.verdox.vcore.gui;

import de.verdox.vcore.nbt.VCoreNBTModule;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.module.VCorePaperModule;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 01.02.2022 19:57
 */
public class VCoreGUIModule extends VCorePaperModule {

    private VCoreNBTModule vCoreNBTModule;

    @Override
    public void enableModule(VCorePaper coreInstance) {
        vCoreNBTModule = VCorePaper.getInstance().getModuleLoader().getModule(VCoreNBTModule.class);
    }

    @Override
    public void disableModule() {

    }

    public VCoreNBTModule getVCoreNBTModule() {
        return vCoreNBTModule;
    }
}
