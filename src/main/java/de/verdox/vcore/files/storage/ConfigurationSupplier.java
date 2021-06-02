package de.verdox.vcore.files.storage;

import de.verdox.vcore.files.config.VCoreConfig;
@FunctionalInterface
public interface ConfigurationSupplier <S extends VCoreConfig<?,?>> {
    /**
     *
     * @param parentFolder ParentFolderPath
     * @return Supplies a new ConfigurationFile
     */
    S supply(String parentFolder);
}
