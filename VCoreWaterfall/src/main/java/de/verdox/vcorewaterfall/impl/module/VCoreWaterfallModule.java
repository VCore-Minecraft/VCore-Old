package de.verdox.vcorewaterfall.impl.module;

import de.verdox.vcore.modules.VCoreModule;
import de.verdox.vcorewaterfall.VCoreWaterfall;
import de.verdox.vcorewaterfall.impl.plugin.VCoreWaterfallSubsystem;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.List;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 06.02.2022 16:27
 */
public abstract class VCoreWaterfallModule implements VCoreModule<Plugin, VCoreWaterfallSubsystem, VCoreWaterfall> {
    @Override
    public final void onPluginEnable() {
        //TODO: Register
        VCoreWaterfall vCoreWaterfall = getCoreInstance();
        vCoreWaterfall.getModuleLoader().registerModule(this);
        enableModule(vCoreWaterfall);
    }

    @Override
    public final void onPluginDisable() {
        disableModule();
    }

    @Override
    public List<VCoreWaterfallSubsystem> provideSubsystems() {
        return null;
    }
}
