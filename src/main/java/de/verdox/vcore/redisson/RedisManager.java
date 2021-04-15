package de.verdox.vcore.redisson;

import de.verdox.vcore.data.datatypes.VCoreData;
import de.verdox.vcore.dataconnection.DataConnection;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.subsystem.VCoreSubsystem;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RedisManager<R extends VCorePlugin<?,?>> {

    private final R plugin;
    private DataConnection.MongoDB mongoDB;
    private final RedissonClient redissonClient;

    public RedisManager (R plugin, boolean clusterMode, String[] addressArray, DataConnection.MongoDB mongoDB){
        this.plugin = plugin;
        getPlugin().consoleMessage("&eStarting Redis Manager");
        this.mongoDB = mongoDB;
        if(this.mongoDB == null)
            throw new IllegalArgumentException("MongoDB can't be null!");
        if(addressArray == null)
            throw new IllegalArgumentException("Address Array null");
        if(addressArray.length == 0)
            throw new IllegalArgumentException("Address Array empty");
        Config config = new Config();
        if(clusterMode){
            config.useClusterServers().addNodeAddress(addressArray);
        }
        else {
            String address = addressArray[0];
            if(address == null)
                throw new IllegalArgumentException("Single Server Adress can't be null!");
            config.useSingleServer().setAddress(addressArray[0]);
        }
        this.redissonClient = Redisson.create(config);
    }

    public DataConnection.MongoDB getMongoDB() {
        return mongoDB;
    }

    public Set<String> getRedisKeys(Class<? extends VCoreData> dataClass){
        return getRedissonClient()
                .getKeys()
                .getKeysStream()
                .filter(s -> s.contains(VCorePlugin.getMongoDBIdentifier(dataClass)))
                .collect(Collectors.toSet());
    }

    public RMap<String, Object> getRedisCache(Class<? extends VCoreData> dataClass, UUID objectUUID){
        return getRedissonClient().getMap("VCoreData:"+objectUUID+":"+VCorePlugin.getMongoDBIdentifier(dataClass));
    }

    public RTopic getTopic(Class<? extends VCoreData> dataClass, UUID objectUUID){
        if(dataClass == null)
            throw new NullPointerException("DataClass is null");
        if(objectUUID == null)
            throw new NullPointerException("objectUUID is null");
        String key = "VCoreDataManager:"+VCorePlugin.getMongoDBIdentifier(dataClass)+":"+objectUUID.toString();
        return getRedissonClient().getTopic(key);
    }

    public String generateSubsystemKey(VCoreSubsystem<?> subsystem, UUID playerUUID){
        if(subsystem == null)
            throw new NullPointerException("subsystem is null");
        if(playerUUID == null)
            throw new NullPointerException("playerUUID is null");
        return "VCoreSessionManager:"+playerUUID.toString()+":"+VCorePlugin.getMongoDBIdentifier(subsystem.getClass());
    }

    public String generateSubsystemKey(Class<? extends VCoreSubsystem<?>> subsystem, UUID playerUUID){
        if(subsystem == null)
            throw new NullPointerException("subsystem is null");
        if(playerUUID == null)
            throw new NullPointerException("playerUUID is null");
        return "VCoreSessionManager:"+playerUUID.toString()+":"+VCorePlugin.getMongoDBIdentifier(subsystem);
    }

    public RTopic getObjectHandlerTopic(VCoreSubsystem<?> subsystem){
        if(subsystem == null)
            throw new NullPointerException("subsystem is null");
        String key = "VCoreDataManager:ObjectHandler"+VCorePlugin.getMongoDBIdentifier(subsystem.getClass());
        return getRedissonClient().getTopic(key);
    }

    public R getPlugin() {
        return plugin;
    }

    public RedissonClient getRedissonClient() {
        return redissonClient;
    }
}
