package de.verdox.vcore.files.storage;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.files.config.VCoreConfig;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import de.verdox.vcore.subsystem.exceptions.SubsystemDeactivatedException;
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

    private VCoreSubsystem<?> subsystem;
    protected String storageName;
    protected String pluginDirectory;
    protected File directory;
    private Map<String, S> saveFiles;

    public StorageSystem(VCoreSubsystem<?> subsystem, String storageName, String pluginDirectory) throws SubsystemDeactivatedException {
        this.subsystem = subsystem;
        VCoreSubsystem.checkSubsystem(getSubsystem());
        this.storageName = storageName;
        this.pluginDirectory = pluginDirectory;
        this.directory = new File(subsystem.getVCorePlugin().getPluginDataFolder()+pluginDirectory,storageName);
        this.saveFiles = new HashMap<>();
        try { Files.createDirectories(Paths.get(directory.getPath())); } catch (IOException e) { e.printStackTrace(); }
        init(loader());
    }

    public abstract ConfigurationLoader<S> loader();

    public void init(ConfigurationLoader<? extends S> configurationLoader) throws SubsystemDeactivatedException {
        VCoreSubsystem.checkSubsystem(getSubsystem());
        subsystem.getVCorePlugin().consoleMessage("&eLoading Storage&7: &a"+storageName);
        try {
            Files
                    .walk(directory.toPath(),1)
                    .skip(1)
                    .forEach(path -> {
                        try {
                            subsystem.getVCorePlugin().consoleMessage("&eFile found&7: &a"+path.toFile().getName());
                            String identifier = FilenameUtils.removeExtension(path.toFile().getName());
                            S saveFile = configurationLoader.load(subsystem.getVCorePlugin(),path.toFile());
                            saveFile.init();
                            saveFiles.put(identifier,saveFile);
                            subsystem.getVCorePlugin().consoleMessage("&eFile loaded&7: &a"+saveFile.getFile().getName());
                        } catch (SubsystemDeactivatedException ignored) {}
                    });
            subsystem.getVCorePlugin().consoleMessage("&eStorage loaded&7: &a"+storageName);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public S getConfiguration(String identifier) throws SubsystemDeactivatedException {
        VCoreSubsystem.checkSubsystem(getSubsystem());
        if(!exist(identifier))
            return null;
        return saveFiles.get(identifier);
    }

    public S createSaveFile(String identifier, ConfigurationSupplier<? extends S> configurationSupplier) throws SubsystemDeactivatedException {
        VCoreSubsystem.checkSubsystem(getSubsystem());
        if(saveFiles.containsKey(identifier))
            return saveFiles.get(identifier);
        S saveFile = configurationSupplier.supply(pluginDirectory+"//"+storageName);
        saveFile.init();
        saveFiles.put(identifier,saveFile);
        return saveFile;
    }

    public boolean deleteSaveFile(String identifier) throws SubsystemDeactivatedException {
        if(!saveFiles.containsKey(identifier))
            return false;
        S saveFile = saveFiles.get(identifier);
        saveFile.delete();
        saveFiles.remove(identifier);
        return true;
    }

    public Set<String> getStorageKeys(){
        if(!subsystem.isActivated())
            return new HashSet<>();
        return saveFiles.keySet();
    }

    public boolean exist(String identifier){
        if(!subsystem.isActivated())
            return false;
        return saveFiles.containsKey(identifier);
    }

    public VCoreSubsystem<?> getSubsystem() {
        return subsystem;
    }
}
