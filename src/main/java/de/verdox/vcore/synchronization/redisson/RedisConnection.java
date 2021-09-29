/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.redisson;

import de.verdox.vcore.plugin.VCorePlugin;
import org.jetbrains.annotations.NotNull;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

import java.util.Objects;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 22:05
 */
public abstract class RedisConnection {
    protected final VCorePlugin<?, ?> plugin;
    protected final RedissonClient redissonClient;

    public RedisConnection(@NotNull VCorePlugin<?, ?> plugin, boolean clusterMode, @NotNull String[] addressArray, String redisPassword) {
        Objects.requireNonNull(plugin, "plugin can't be null!");
        Objects.requireNonNull(addressArray, "addressArray can't be null!");
        Objects.requireNonNull(redisPassword, "redisPassword can't be null!");
        this.plugin = plugin;
        if (addressArray.length == 0)
            throw new IllegalArgumentException("Address Array empty");
        Config config = new Config();
        if (clusterMode) {
            ClusterServersConfig clusterServersConfig = config.useClusterServers();
            clusterServersConfig.addNodeAddress(addressArray);

            if (redisPassword != null && !redisPassword.isEmpty())
                clusterServersConfig.addNodeAddress(addressArray).setPassword(redisPassword);
            else
                clusterServersConfig.addNodeAddress(addressArray);
        } else {
            String address = addressArray[0];
            if (address == null)
                throw new IllegalArgumentException("Single Server Adress can't be null!");
            SingleServerConfig singleServerConfig = config.useSingleServer();
            singleServerConfig.setSubscriptionsPerConnection(30);

            if (redisPassword != null && !redisPassword.isEmpty())
                singleServerConfig.setAddress(addressArray[0]).setPassword(redisPassword);
            else
                singleServerConfig.setAddress(addressArray[0]);
        }
        config.setNettyThreads(4);
        config.setThreads(4);
        this.redissonClient = Redisson.create(config);
    }
}
