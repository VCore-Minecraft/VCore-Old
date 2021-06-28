package de.verdox.vcore.plugin.files.config.bukkit;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import de.verdox.vcore.plugin.files.config.VCoreConfig;
import de.verdox.vcore.plugin.files.config.serialization.VCoreDeserializer;
import de.verdox.vcore.plugin.files.config.serialization.VCoreSerializable;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class VCoreBukkitConfig extends VCoreConfig<FileConfiguration> {
    public VCoreBukkitConfig(VCorePlugin.Minecraft plugin, File file) {
        super(plugin,file);
    }

    public VCoreBukkitConfig(VCorePlugin.Minecraft plugin, String fileName, String pluginDirectory) {
        super(plugin,fileName,pluginDirectory);
    }

    @Override
    public FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public FileConfiguration create() {
        return YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void saveSerializable(String path, VCoreSerializable vCoreSerializable) {
        config.set(path,null);
        Map<String, Object> data = vCoreSerializable.serialize();

        data.keySet().forEach(key -> {
            config.set(path+"."+key,data.get(key));
        });
        save();
    }

    @Override
    public <S extends VCoreSerializable> S getSerializable(Class<S> serializableClass, String path, VCoreDeserializer<? extends S> vCoreDeserializer) {
        Map<String, Object> data = new HashMap<>();
        if(!config.isConfigurationSection(path))
            return null;

        config.getConfigurationSection(path).getKeys(false).forEach(key -> {
            data.put(key,config.get(path+"."+key));
        });
        return vCoreDeserializer.deSerialize(data);
    }

    @Override
    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void delete() {
        try { FileUtils.forceDelete(file); } catch (IOException e) { e.printStackTrace(); }
    }
}
