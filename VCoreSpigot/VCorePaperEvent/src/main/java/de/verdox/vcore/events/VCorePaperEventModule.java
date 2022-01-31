/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.events;

import de.verdox.vcore.events.paper.CustomPaperEventListener;
import de.verdox.vcore.modules.VCoreModule;
import de.verdox.vcore.plugin.VCorePlugin;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 31.01.2022 20:31
 */
public class VCorePaperEventModule implements VCoreModule<VCorePlugin.Minecraft> {

    private CustomPaperEventListener customPaperEventListener;

    @Override
    public void enableModule(VCorePlugin.Minecraft plugin) {
        customPaperEventListener = new CustomPaperEventListener(plugin);
    }

    @Override
    public void disableModule() {

    }

    public CustomPaperEventListener getCustomPaperEventListener() {
        return customPaperEventListener;
    }
}
