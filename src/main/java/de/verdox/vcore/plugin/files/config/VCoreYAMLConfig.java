/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.files.config;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.files.config.serialization.VCoreDeserializer;
import de.verdox.vcore.plugin.files.config.serialization.VCoreSerializable;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import java.io.IOException;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 27.06.2021 20:00
 */
public abstract class VCoreYAMLConfig extends VCoreConfig<YamlFile> {

    public VCoreYAMLConfig(VCorePlugin<?, ?> plugin, String fileName, String pluginDirectory) {
        super(plugin, fileName, pluginDirectory);
    }

    @Override
    public YamlFile create() {
        YamlFile yamlFile = new YamlFile(getPlugin().getPluginDataFolder()+"//"+pluginDirectory+"//"+fileName);
        try {
            yamlFile.createOrLoadWithComments();
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
            return null;
        }
        yamlFile.options().copyDefaults(true);
        yamlFile.options().copyHeader(true);
        return yamlFile;
    }

    @Override
    public YamlFile getConfig() {
        return config;
    }

    @Override
    public void save() {
        try {
            config.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete() {
        try {
            config.deleteFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveSerializable(String path, VCoreSerializable vCoreSerializable) {

    }

    @Override
    public <S extends VCoreSerializable> S getSerializable(Class<S> serializableClass, String path, VCoreDeserializer<? extends S> vCoreDeserializer) {
        return null;
    }
}
