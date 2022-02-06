package de.verdox.vcorepaper.module;

import de.verdox.vcore.modules.VCoreModule;
import de.verdox.vcore.modules.VCoreModuleLoader;
import de.verdox.vcore.plugin.VCoreCoreInstance;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.impl.plugin.VCorePaperPlugin;
import de.verdox.vcorepaper.impl.plugin.VCorePaperSubsystem;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.02.2022 16:30
 */
public abstract class VCorePaperModule extends VCorePaperPlugin implements VCoreModule<JavaPlugin,VCorePaperSubsystem,VCorePaper> {
    @Override
    public final void onPluginEnable() {
        //TODO: Register
        VCorePaper vCorePaper = getCoreInstance();
        vCorePaper.getModuleLoader().registerModule(this);
        enableModule(vCorePaper);
    }

    @Override
    public final void onPluginDisable() {
        disableModule();
    }

    @Override
    public List<VCorePaperSubsystem> provideSubsystems() {
        return null;
    }
}
