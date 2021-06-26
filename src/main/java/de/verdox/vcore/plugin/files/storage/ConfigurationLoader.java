package de.verdox.vcore.plugin.files.storage;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.files.config.VCoreConfig;

import java.io.File;

public interface ConfigurationLoader<S extends VCoreConfig<?,?>>{
    S load(VCorePlugin<?,?> plugin, File file);
}
