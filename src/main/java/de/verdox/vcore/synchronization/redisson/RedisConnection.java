/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.redisson;

import de.verdox.vcore.plugin.VCorePlugin;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import javax.annotation.Nonnull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 22:05
 */
public abstract class RedisConnection {
    protected final VCorePlugin<?,?> plugin;
    protected final RedissonClient redissonClient;

    public RedisConnection (@Nonnull VCorePlugin<?,?> plugin, boolean clusterMode, @Nonnull String[] addressArray, String redisPassword){
        this.plugin = plugin;
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
        config.setNettyThreads(4);
        config.setThreads(8);
        this.redissonClient = Redisson.create(config);
    }
}
