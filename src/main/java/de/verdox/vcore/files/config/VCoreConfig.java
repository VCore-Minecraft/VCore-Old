package de.verdox.vcore.files.config;

import de.verdox.vcore.subsystem.VCoreSubsystem;
import de.verdox.vcore.subsystem.exceptions.SubsystemDeactivatedException;
import de.verdox.vcore.files.config.bukkit.VCoreBukkitConfig;
import de.verdox.vcore.files.config.bungeecord.VCoreBungeeConfig;
import de.verdox.vcore.files.config.serialization.VCoreDeserializer;
import de.verdox.vcore.files.config.serialization.VCoreSerializable;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

public abstract class VCoreConfig <R extends VCoreSubsystem<?>,T> {

    private final R subSystem;
    protected String fileName;
    protected String pluginDirectory;
    protected File file;
    protected T config;

    public VCoreConfig(R subSystem, File file) throws SubsystemDeactivatedException {
        this.subSystem = subSystem;
        this.file = file;
        if(!subSystem.isActivated())
            return;
        this.fileName = FilenameUtils.removeExtension(file.getName());
        String[] split = file.getPath().split(getSubSystem().getVCorePlugin().getPluginDataFolder().getPath());
        this.pluginDirectory = split[split.length-1];
        this.config = getConfig();
    }
    public VCoreConfig(R subSystem, String fileName, String pluginDirectory) throws SubsystemDeactivatedException {
        this.subSystem = subSystem;
        this.file = new File(getSubSystem().getVCorePlugin().getPluginDataFolder() + pluginDirectory, fileName);
        if(!subSystem.isActivated())
            return;
        this.fileName = fileName;
        this.pluginDirectory = pluginDirectory;

        this.config = getConfig();
    }

    public File getFile() {
        return file;
    }

    public abstract T getConfig() throws SubsystemDeactivatedException;
    public abstract void save() throws SubsystemDeactivatedException;
    public abstract void delete() throws SubsystemDeactivatedException;

    public abstract void saveSerializable(String path, VCoreSerializable vCoreSerializable) throws SubsystemDeactivatedException;
    public abstract <S extends VCoreSerializable> S getSerializable(Class<S> serializableClass, String path, VCoreDeserializer<? extends S> vCoreDeserializer) throws SubsystemDeactivatedException;

    public void init() throws SubsystemDeactivatedException {
        setupConfig();
        save();
        onInit();
    }
    public abstract void onInit();
    public abstract void setupConfig() throws SubsystemDeactivatedException;

    public R getSubSystem() {
        return subSystem;
    }

    public abstract static class Bukkit extends VCoreBukkitConfig {
        public Bukkit(VCoreSubsystem.Bukkit subsystem, File file) throws SubsystemDeactivatedException {
            super(subsystem, file);
        }
        public Bukkit(VCoreSubsystem.Bukkit subsystem, String fileName, String pluginDirectory) throws SubsystemDeactivatedException {
            super(subsystem,fileName,pluginDirectory);
        }
    }

    public abstract static class BungeeCord extends VCoreBungeeConfig {
        public BungeeCord(VCoreSubsystem.BungeeCord subsystem, File file) throws SubsystemDeactivatedException {
            super(subsystem,file);
        }
        public BungeeCord(VCoreSubsystem.BungeeCord subsystem, String fileName, String pluginDirectory) throws SubsystemDeactivatedException {
            super(subsystem,fileName,pluginDirectory);
        }
    }

}
