/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.files.config.VCoreYAMLConfig;
import de.verdox.vcore.synchronization.messaging.redis.RedisMessaging;

import java.util.List;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 29.06.2021 23:14
 */
public class MessagingConfig extends VCoreYAMLConfig {
    public MessagingConfig(VCorePlugin<?, ?> plugin, String fileName, String pluginDirectory) {
        super(plugin, fileName, pluginDirectory);
    }

    public MessagingService<?> constructMessagingService() {
        String messagingType = config.getString("MessagingService.type");
        getPlugin().consoleMessage("&eMessagingService&7: &b" + messagingType, false);
        if (messagingType.equalsIgnoreCase("redis")) {
            boolean useCluster = config.getBoolean("MessagingService.redis.useCluster");
            String[] addresses = config.getStringList("MessagingService.redis.addresses").toArray(new String[0]);
            String password = config.getString("MessagingService.redis.password");

            if (useCoreMessagingService()) {
                plugin.consoleMessage("&eSelecting Core MessagingService", false);
                return plugin.getCoreInstance().getServices().getMessagingService();
            } else
                return new RedisMessaging(getPlugin(), useCluster, addresses, password);
        }
        throw new IllegalStateException("MessagingType " + messagingType + " not implemented yet");
    }

    @Override
    public void onInit() {

    }

    public boolean useCoreMessagingService() {
        boolean useCoreInstance = config.getBoolean("MessagingService.useCoreInstance");
        if (plugin.equals(plugin.getCoreInstance()))
            useCoreInstance = false;
        return useCoreInstance;
    }

    @Override
    public void setupConfig() {
        config.addDefault("MessagingService.type", "redis");
        config.addDefault("MessagingService.useCoreInstance", true);
        config.addDefault("MessagingService.redis.useCluster", false);
        config.addDefault("MessagingService.redis.addresses", List.of("redis://localhost:6379"));
        config.addDefault("MessagingService.redis.password", "");
        save();
    }
}
