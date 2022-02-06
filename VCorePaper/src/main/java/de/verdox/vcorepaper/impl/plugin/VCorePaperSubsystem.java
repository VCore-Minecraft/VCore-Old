package de.verdox.vcorepaper.impl.plugin;

import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.02.2022 20:53
 */
public abstract class VCorePaperSubsystem extends VCoreSubsystem<VCorePaperPlugin> {
    public VCorePaperSubsystem(VCorePaperPlugin plugin) {
        super(plugin);
    }
}