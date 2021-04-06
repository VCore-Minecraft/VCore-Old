package de.verdox.vcore.files.storage;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.files.config.VCoreConfig;

import java.io.File;

public interface ConfigurationLoader <S extends VCoreConfig<?,?>>{

    S load(VCorePlugin<?,?> plugin, File file);

}
