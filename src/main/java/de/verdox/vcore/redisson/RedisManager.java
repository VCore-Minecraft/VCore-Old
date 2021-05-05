package de.verdox.vcore.redisson;

import de.verdox.vcore.data.annotations.DataContext;
import de.verdox.vcore.data.annotations.PreloadStrategy;
import de.verdox.vcore.data.annotations.VCoreDataContext;
import de.verdox.vcore.data.datatypes.VCoreData;
import de.verdox.vcore.dataconnection.DataConnection;
import de.verdox.vcore.plugin.VCorePlugin;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.UUID;

public class RedisManager<R extends VCorePlugin<?,?>> {

    private final R plugin;
    private DataConnection.MongoDB mongoDB;
    private final RedissonClient redissonClient;

    public RedisManager (R plugin, boolean clusterMode, String[] addressArray, String redisPassword, DataConnection.MongoDB mongoDB){
        this.plugin = plugin;
        getPlugin().consoleMessage("&eStarting Redis Manager",true);
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

            if(redisPassword != null && !redisPassword.isEmpty())
                config.useClusterServers().addNodeAddress(addressArray).setPassword(redisPassword);
            else
                config.useClusterServers().addNodeAddress(addressArray);
        }
        else {
            String address = addressArray[0];
            if(address == null)
                throw new IllegalArgumentException("Single Server Adress can't be null!");

            if(redisPassword != null && !redisPassword.isEmpty())
                config.useSingleServer().setAddress(addressArray[0]).setPassword(redisPassword);
            else
                config.useSingleServer().setAddress(addressArray[0]);
        }
        this.redissonClient = Redisson.create(config);
    }

    public DataConnection.MongoDB getMongoDB() {
        return mongoDB;
    }

    public RMap<String, Object> getRedisCache(Class<? extends VCoreData> dataClass, UUID objectUUID){
        return getRedissonClient().getMap(plugin.getPluginName()+"Cache:"+objectUUID+":"+VCorePlugin.getMongoDBIdentifier(dataClass));
    }

    public RTopic getTopic(Class<? extends VCoreData> dataClass, UUID objectUUID){
        if(dataClass == null)
            throw new NullPointerException("DataClass is null");
        if(objectUUID == null)
            throw new NullPointerException("objectUUID is null");
        String key = plugin.getPluginName()+"DataTopic:"+VCorePlugin.getMongoDBIdentifier(dataClass)+":"+objectUUID.toString();
        return getRedissonClient().getTopic(key);
    }

    public R getPlugin() {
        return plugin;
    }

    public DataContext getContext(Class<? extends VCoreData> dataClass){
        VCoreDataContext vCoreDataContext = dataClass.getAnnotation(VCoreDataContext.class);
        if(vCoreDataContext == null)
            return DataContext.GLOBAL;
        return vCoreDataContext.dataContext();
    }

    public PreloadStrategy getPreloadStrategy(Class<? extends VCoreData> dataClass){
        VCoreDataContext vCoreDataContext = dataClass.getAnnotation(VCoreDataContext.class);
        if(vCoreDataContext == null)
            return PreloadStrategy.LOAD_ON_NEED;
        return vCoreDataContext.preloadStrategy();
    }

    public RedissonClient getRedissonClient() {
        return redissonClient;
    }
}
