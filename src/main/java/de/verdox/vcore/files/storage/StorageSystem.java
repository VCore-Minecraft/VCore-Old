package de.verdox.vcore.files.storage;

import de.verdox.vcore.files.config.VCoreConfig;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class StorageSystem<S extends VCoreConfig<?,?>> {
    protected String storageName;
    protected String pluginDirectory;
    protected File directory;
    private Map<String, S> saveFiles;
    private VCorePlugin<?, ?> plugin;

    public StorageSystem(VCorePlugin<?,?> plugin, String storageName, String pluginDirectory) {
        this.plugin = plugin;
        this.storageName = storageName;
        this.pluginDirectory = pluginDirectory;
        this.directory = new File(plugin.getPluginDataFolder()+pluginDirectory,storageName);
        this.saveFiles = new HashMap<>();
        try { Files.createDirectories(Paths.get(directory.getPath())); } catch (IOException e) { e.printStackTrace(); }
    }

    protected abstract ConfigurationLoader<S> load();
    public abstract void onLoad();

    public void init(){
        loadStorage(load());
    }

    private void loadStorage(ConfigurationLoader<? extends S> configurationLoader) {
        plugin.consoleMessage("&eLoading Storage&7: &a"+storageName,1,false);
        try {
            Files
                    .walk(directory.toPath(),1)
                    .skip(1)
                    .forEach(path -> {
                        //plugin.consoleMessage("&eFile found&7: &a"+path.toFile().getName());
                        String identifier = FilenameUtils.removeExtension(path.toFile().getName());
                        S saveFile = configurationLoader.load(plugin,path.toFile());
                        if(saveFile == null)
                            throw new NullPointerException("Config file could not be loaded: "+path);
                        saveFile.init();
                        saveFiles.put(identifier,saveFile);
                        plugin.consoleMessage("&eFile loaded&7: &a"+saveFile.getFile().getName(),true);
                    });
            plugin.consoleMessage("&eStorage loaded&7: &a"+storageName,1,false);
            onLoad();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public S getConfiguration(String identifier) {
        if(!exist(identifier))
            return null;
        return saveFiles.get(identifier);
    }

    public S createSaveFile(String identifier, ConfigurationSupplier<? extends S> configurationSupplier) {
        if(saveFiles.containsKey(identifier))
            return saveFiles.get(identifier);
        S saveFile = configurationSupplier.supply(pluginDirectory+"//"+storageName);
        saveFile.init();
        saveFiles.put(identifier,saveFile);
        return saveFile;
    }

    public boolean deleteSaveFile(String identifier) {
        if(!saveFiles.containsKey(identifier))
            return false;
        S saveFile = saveFiles.get(identifier);
        saveFile.delete();
        saveFiles.remove(identifier);
        return true;
    }

    public Set<String> getStorageKeys(){
        return saveFiles.keySet();
    }

    public boolean exist(String identifier){
        return saveFiles.containsKey(identifier);
    }

    public VCorePlugin<?, ?> getPlugin() {
        return plugin;
    }
}
