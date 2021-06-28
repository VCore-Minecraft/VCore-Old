package de.verdox.vcore.plugin.files.config;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import de.verdox.vcore.plugin.files.config.bukkit.VCoreBukkitConfig;
import de.verdox.vcore.plugin.files.config.bungeecord.VCoreBungeeConfig;
import de.verdox.vcore.plugin.files.config.serialization.VCoreDeserializer;
import de.verdox.vcore.plugin.files.config.serialization.VCoreSerializable;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

public abstract class VCoreConfig <T> {
    private final VCorePlugin<?,?> plugin;
    protected String fileName;
    protected String pluginDirectory;
    protected File file;
    protected T config;

    public VCoreConfig(VCorePlugin<?,?> plugin, File file) {
        this.plugin = plugin;
        this.file = file;
        this.fileName = FilenameUtils.removeExtension(file.getName());
        String[] split = file.getPath().split(plugin.getPluginDataFolder().getPath());
        this.pluginDirectory = split[split.length-1];
    }

    public VCoreConfig(VCorePlugin<?,?> plugin, String fileName, String pluginDirectory) {
        this.plugin = plugin;
        this.file = new File(plugin.getPluginDataFolder() + pluginDirectory, fileName);
        this.fileName = fileName;
        this.pluginDirectory = pluginDirectory;
    }

    public File getFile() {
        return file;
    }

    public abstract T getConfig();
    public abstract void save();
    public abstract void delete();

    public abstract void saveSerializable(String path, VCoreSerializable vCoreSerializable);
    public abstract <S extends VCoreSerializable> S getSerializable(Class<S> serializableClass, String path, VCoreDeserializer<? extends S> vCoreDeserializer);

    public void init() {
        this.config = create();
        setupConfig();
        save();
        onInit();
    }
    public abstract T create();
    public abstract void onInit();
    public abstract void setupConfig();

    public VCorePlugin<?, ?> getPlugin() {
        return plugin;
    }

    public abstract static class Bukkit extends VCoreBukkitConfig {
        public Bukkit(VCorePlugin.Minecraft plugin, File file) {
            super(plugin, file);
        }
        public Bukkit(VCorePlugin.Minecraft plugin, String fileName, String pluginDirectory) {
            super(plugin,fileName,pluginDirectory);
        }
    }

    public abstract static class BungeeCord extends VCoreBungeeConfig {
        public BungeeCord(VCorePlugin.BungeeCord plugin, File file) {
            super(plugin,file);
        }
        public BungeeCord(VCorePlugin.BungeeCord plugin, String fileName, String pluginDirectory) { super(plugin,fileName,pluginDirectory); }
    }

}
