/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.redisson;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.pipeline.datatypes.VCoreData;
import de.verdox.vcore.util.global.AnnotationResolver;
import org.jetbrains.annotations.NotNull;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

import java.util.Objects;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 22:05
 */
public class RedisConnection {
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

            if (!redisPassword.isEmpty())
                clusterServersConfig.addNodeAddress(addressArray).setPassword(redisPassword);
            else
                clusterServersConfig.addNodeAddress(addressArray);
        } else {
            SingleServerConfig singleServerConfig = config.useSingleServer();
            singleServerConfig.setSubscriptionsPerConnection(30);

            if (!redisPassword.isEmpty())
                singleServerConfig.setAddress(addressArray[0]).setPassword(redisPassword);
            else
                singleServerConfig.setAddress(addressArray[0]);
        }
        config.setNettyThreads(4);
        config.setThreads(4);
        this.redissonClient = Redisson.create(config);
    }

    public RTopic getTopic(@NotNull Class<? extends VCoreData> dataClass) {
        Objects.requireNonNull(dataClass, "dataClass can't be null!");
        String key = plugin.getPluginName() + "DataTopic:" + AnnotationResolver.getDataStorageIdentifier(dataClass);
        return redissonClient.getTopic(key, new SerializationCodec());
    }
}
