package de.verdox.vcore.files.config.bukkit;

import de.verdox.vcore.subsystem.VCoreSubsystem;
import de.verdox.vcore.subsystem.exceptions.SubsystemDeactivatedException;
import de.verdox.vcore.files.config.VCoreConfig;
import de.verdox.vcore.files.config.serialization.VCoreDeserializer;
import de.verdox.vcore.files.config.serialization.VCoreSerializable;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class VCoreBukkitConfig extends VCoreConfig<VCoreSubsystem.Bukkit,FileConfiguration> {

    public VCoreBukkitConfig(VCoreSubsystem.Bukkit subsystem, File file) throws SubsystemDeactivatedException {
        super(subsystem,file);
    }
    public VCoreBukkitConfig(VCoreSubsystem.Bukkit subsystem, String fileName, String pluginDirectory) throws SubsystemDeactivatedException {
        super(subsystem,fileName,pluginDirectory);
    }

    @Override
    public FileConfiguration getConfig() throws SubsystemDeactivatedException {
        VCoreSubsystem.checkSubsystem(getSubSystem());
        return YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void saveSerializable(String path, VCoreSerializable vCoreSerializable) throws SubsystemDeactivatedException {
        VCoreSubsystem.checkSubsystem(getSubSystem());
        config.set(path,null);
        Map<String, Object> data = vCoreSerializable.serialize();

        data.keySet().forEach(key -> {
            config.set(path+"."+key,data.get(key));
        });
        save();
    }

    @Override
    public <S extends VCoreSerializable> S getSerializable(Class<S> serializableClass, String path, VCoreDeserializer<? extends S> vCoreDeserializer) throws SubsystemDeactivatedException {
        VCoreSubsystem.checkSubsystem(getSubSystem());
        Map<String, Object> data = new HashMap<>();
        if(!config.isConfigurationSection(path))
            return null;

        config.getConfigurationSection(path).getKeys(false).forEach(key -> {
            data.put(key,config.get(path+"."+key));
        });
        return vCoreDeserializer.deSerialize(data);
    }

    @Override
    public void save() throws SubsystemDeactivatedException {
        VCoreSubsystem.checkSubsystem(getSubSystem());
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete() throws SubsystemDeactivatedException {
        VCoreSubsystem.checkSubsystem(getSubSystem());
        try { FileUtils.forceDelete(file); } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public VCoreSubsystem.Bukkit getSubSystem() {
        return super.getSubSystem();
    }
}
