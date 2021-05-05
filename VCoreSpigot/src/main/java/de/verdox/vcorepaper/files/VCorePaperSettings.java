package de.verdox.vcorepaper.files;

import de.verdox.vcore.files.config.VCoreConfig;
import de.verdox.vcorepaper.VCorePaper;

import java.util.Arrays;
import java.util.List;

public class VCorePaperSettings extends VCoreConfig.Bukkit{
    public VCorePaperSettings(VCorePaper plugin, String fileName, String pluginDirectory) {
        super(plugin, fileName, pluginDirectory);
    }

    @Override
    public void onInit() {

    }

    @Override
    public void setupConfig() {

        config.addDefault("DataConnection.Redis.addresses", Arrays.asList("localhost:6379"));
        config.addDefault("DataConnection.Redis.useCluster", false);
        config.addDefault("DataConnection.Redis.password", "password");

        config.addDefault("DataConnection.MongoDB.host", "localhost");
        config.addDefault("DataConnection.MongoDB.database", "vcore");
        config.addDefault("DataConnection.MongoDB.port", 27017);
        config.addDefault("DataConnection.MongoDB.username", "");
        config.addDefault("DataConnection.MongoDB.password", "");

        config.options().copyDefaults(true);
        save();
    }

    public List<String> getRedisAddresses(){
        return config.getStringList("DataConnection.Redis.addresses");
    }

    public boolean useRedisCluster(){
        return config.getBoolean("DataConnection.Redis.useCluster");
    }

    public String getRedisPassword(){
        return config.getString("DataConnection.Redis.password");
    }

    public String getMongoDBHost(){
        return config.getString("DataConnection.MongoDB.host");
    }

    public String getMongoDBDatabase(){
        return config.getString("DataConnection.MongoDB.database");
    }

    public int getMongoDBPort(){
        return config.getInt("DataConnection.MongoDB.port");
    }

    public String getMongoDBUsername(){
        return config.getString("DataConnection.MongoDB.username");
    }

    public String getMongoDBPassword(){
        return config.getString("DataConnection.MongoDB.password");
    }
}
