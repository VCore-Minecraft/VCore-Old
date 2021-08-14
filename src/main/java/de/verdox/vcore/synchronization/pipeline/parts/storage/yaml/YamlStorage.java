/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.parts.storage.yaml;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.files.config.VCoreYAMLConfig;
import de.verdox.vcore.synchronization.pipeline.datatypes.NetworkData;
import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.synchronization.pipeline.parts.storage.GlobalStorage;
import de.verdox.vcore.util.global.AnnotationResolver;
import org.apache.commons.io.FilenameUtils;
import org.bson.BsonDocumentReader;
import org.jongo.bson.Bson;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 14.08.2021 17:43
 */
public class YamlStorage implements GlobalStorage {

    private VCorePlugin<?, ?> plugin;

    public YamlStorage(VCorePlugin<?,?> plugin){
        this.plugin = plugin;
        plugin.consoleMessage("&eYAML Storage initialized",true);
    }

    @Override
    public Map<String, Object> loadData(@Nonnull Class<? extends VCoreData> dataClass, @Nonnull UUID objectUUID) {
        Map<String, Object> data = new HashMap<>();
        if(!dataExist(dataClass,objectUUID))
            return data;
        VCoreYAMLConfig vCoreYAMLConfig = getSaveFile(dataClass,objectUUID);
        vCoreYAMLConfig.getConfig().getKeys(false).forEach(s -> {
            Object readData = vCoreYAMLConfig.getConfig().get(s);
            if(readData instanceof String){
                if(((String) readData).contains("VCoreStorageType.java.util.UUID "))
                    data.put(s,UUID.fromString(((String) readData).replace("VCoreStorageType.java.util.UUID ","")));
            }
            data.put(s, readData);
        });
        return data;
    }

    @Override
    public boolean dataExist(@Nonnull Class<? extends VCoreData> dataClass, @Nonnull UUID objectUUID) {
        return getSaveFile(dataClass,objectUUID).getFile().exists();
    }

    @Override
    public void save(@Nonnull Class<? extends VCoreData> dataClass, @Nonnull UUID objectUUID, @Nonnull Map<String, Object> dataToSave) {
        VCoreYAMLConfig vCoreYAMLConfig = getSaveFile(dataClass,objectUUID);
        vCoreYAMLConfig.init();
        dataToSave.forEach((s, o) -> {
            if(o instanceof UUID)
                vCoreYAMLConfig.getConfig().set(s,"'VCoreStorageType.java.util.UUID "+o+"'");
            else
                vCoreYAMLConfig.getConfig().set(s, o);
        });
        vCoreYAMLConfig.save();
    }

    @Override
    public boolean remove(@Nonnull Class<? extends VCoreData> dataClass, @Nonnull UUID objectUUID) {
        if(!dataExist(dataClass,objectUUID))
            return false;
        return getSaveFile(dataClass,objectUUID).getFile().delete();
    }

    @Override
    public Set<UUID> getSavedUUIDs(@Nonnull Class<? extends VCoreData> dataClass) {
        Set<UUID> foundUUIDs = new HashSet<>();
        VCoreYAMLConfig dummyConfig = getSaveFile(dataClass,UUID.randomUUID());
        File parentFolder = dummyConfig.getFile().getParentFile();
        if(!parentFolder.exists())
            return foundUUIDs;
        try {
            Files.walk(parentFolder.toPath(),1).forEach(path -> {
                String fileName = FilenameUtils.removeExtension(path.toFile().getName());
                try{
                    UUID readUUID = UUID.fromString(fileName);
                    foundUUIDs.add(readUUID);
                }
                catch (IllegalArgumentException e){
                    plugin.consoleMessage("&cCould not read file name in YAMLStorage because it is not a uuid&7: &e"+path.toFile().getAbsolutePath(),false);
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return foundUUIDs;
    }

    /**
     * Returns the VCoreYamlConfig object (not initialized)
     * @param dataClass VCoreDataClass
     * @param objectUUID ObjectUUID
     * @return the VCoreYamlConfig instance
     */
    private VCoreYAMLConfig getSaveFile(@Nonnull Class<? extends VCoreData> dataClass, @Nonnull UUID objectUUID){
        String storageIdentifier = AnnotationResolver.getDataStorageIdentifier(dataClass);
        String prefix;
        if(NetworkData.class.isAssignableFrom(dataClass))
            prefix = "VCore_NetworkData";
        else
            prefix = AnnotationResolver.findDependSubsystemClass(dataClass).getSimpleName();

        return new VCoreYAMLConfig(plugin,objectUUID.toString(),"//pipeline//yamlStorage//"+prefix+"//"+storageIdentifier);
    }
}
