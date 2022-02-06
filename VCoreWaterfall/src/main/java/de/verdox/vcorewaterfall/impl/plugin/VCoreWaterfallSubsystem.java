package de.verdox.vcorewaterfall.impl.plugin;

import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.02.2022 22:37
 */
public abstract class VCoreWaterfallSubsystem extends VCoreSubsystem<VCoreWaterfallPlugin> {
    public VCoreWaterfallSubsystem(VCoreWaterfallPlugin VCorePlugin) {
        super(VCorePlugin);
    }
}
