package de.verdox.vcore.files.config.bungeecord;

import de.verdox.vcore.subsystem.VCoreSubsystem;
import de.verdox.vcore.files.config.VCoreConfig;
import de.verdox.vcore.files.config.serialization.VCoreDeserializer;
import de.verdox.vcore.files.config.serialization.VCoreSerializable;
import de.verdox.vcore.subsystem.exceptions.SubsystemDeactivatedException;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class VCoreBungeeConfig extends VCoreConfig<VCoreSubsystem.BungeeCord,Configuration> {

    public VCoreBungeeConfig(VCoreSubsystem.BungeeCord subsystem, File file) throws SubsystemDeactivatedException {
        super(subsystem,file);
    }
    public VCoreBungeeConfig(VCoreSubsystem.BungeeCord subsystem, String fileName, String pluginDirectory) throws SubsystemDeactivatedException {
        super(subsystem,fileName,pluginDirectory);
    }

    @Override
    public Configuration getConfig() {
        if(!getSubSystem().isActivated())
            return null;
        try{
            if(!file.exists())
                if(!file.createNewFile())
                    return null;
            return ConfigurationProvider.getProvider(net.md_5.bungee.config.YamlConfiguration.class).load(file); }
        catch (IOException e){return null;}
    }

    @Override
    public void saveSerializable(String path, VCoreSerializable vCoreSerializable) {
        config.set(path,null);
        Map<String, Object> data = vCoreSerializable.serialize();
        data.keySet().forEach(key -> {
            config.set(path+"."+key,data.get(key));
        });
    }

    @Override
    public <S extends VCoreSerializable> S getSerializable(Class<S> serializableClass, String path, VCoreDeserializer<? extends S> vCoreDeserializer) {
        Map<String, Object> data = new HashMap<>();
        if(!config.contains(path))
            return null;

        config.getKeys().forEach(key -> {
            if(!key.startsWith(path))
                return;
            data.put(key,config.get(path+"."+key));
        });
        return vCoreDeserializer.deSerialize(data);
    }

    @Override
    public void save() {}

    @Override
    public void delete() {
        try { FileUtils.forceDelete(file); } catch (IOException e) { e.printStackTrace(); }
    }

}
