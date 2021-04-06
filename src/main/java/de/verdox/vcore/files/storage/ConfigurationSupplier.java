package de.verdox.vcore.files.storage;

import de.verdox.vcore.files.config.VCoreConfig;

@FunctionalInterface
public interface ConfigurationSupplier <S extends VCoreConfig<?,?>> {
    S supply(String parentFolder);
}
