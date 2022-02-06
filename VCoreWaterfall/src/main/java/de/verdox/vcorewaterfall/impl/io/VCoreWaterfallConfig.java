package de.verdox.vcorewaterfall.impl.io;

import de.verdox.vcore.plugin.files.config.VCoreConfig;
import de.verdox.vcore.plugin.files.config.serialization.VCoreDeserializer;
import de.verdox.vcore.plugin.files.config.serialization.VCoreSerializable;
import de.verdox.vcorewaterfall.impl.plugin.VCoreWaterfallPlugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.02.2022 22:47
 */
public abstract class VCoreWaterfallConfig extends VCoreConfig<Configuration> {
    public VCoreWaterfallConfig(VCoreWaterfallPlugin plugin, File file) {
        super(plugin, file);
    }

    public VCoreWaterfallConfig(VCoreWaterfallPlugin plugin, String fileName, String pluginDirectory) {
        super(plugin, fileName, pluginDirectory);
    }

    @Override
    public Configuration create() {
        try {
            if (!file.exists())
                if (!file.createNewFile())
                    return null;
            return ConfigurationProvider.getProvider(net.md_5.bungee.config.YamlConfiguration.class).load(file);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public Configuration getConfig() {
        try {
            if (!file.exists())
                if (!file.createNewFile())
                    return null;
            return ConfigurationProvider.getProvider(net.md_5.bungee.config.YamlConfiguration.class).load(file);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void saveSerializable(String path, VCoreSerializable vCoreSerializable) {
        config.set(path, null);
        Map<String, Object> data = vCoreSerializable.serialize();
        data.keySet().forEach(key -> {
            config.set(path + "." + key, data.get(key));
        });
    }

    @Override
    public <S extends VCoreSerializable> S getSerializable(Class<S> serializableClass, String path, VCoreDeserializer<? extends S> vCoreDeserializer) {
        Map<String, Object> data = new HashMap<>();
        if (!config.contains(path))
            return null;

        config.getKeys().forEach(key -> {
            if (!key.startsWith(path))
                return;
            data.put(key, config.get(path + "." + key));
        });
        return vCoreDeserializer.deSerialize(data);
    }

    @Override
    public void save() {
    }

    @Override
    public void delete() {
        try {
            FileUtils.forceDelete(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
