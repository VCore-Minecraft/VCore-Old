package de.verdox.vcore.plugin.files.storage;

import de.verdox.vcore.plugin.files.config.VCoreConfig;
@FunctionalInterface
public interface ConfigurationSupplier <S extends VCoreConfig<?>> {
    /**
     *
     * @param parentFolder ParentFolderPath
     * @return Supplies a new ConfigurationFile
     */
    S supply(String parentFolder);
}
