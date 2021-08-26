/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging.redis;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.messaging.MessagingService;
import de.verdox.vcore.synchronization.messaging.event.MessageEvent;
import de.verdox.vcore.synchronization.messaging.instructions.InstructionService;
import de.verdox.vcore.synchronization.messaging.messages.Message;
import de.verdox.vcore.synchronization.redisson.RedisConnection;
import org.redisson.api.RTopic;
import org.redisson.api.listener.MessageListener;
import org.redisson.codec.SerializationCodec;

import javax.annotation.Nonnull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 22:09
 */
public class RedisMessaging extends RedisConnection implements MessagingService<RedisMessageBuilder> {
    private final RTopic globalMessagingChannel;
    private final MessageListener<Message> messageListener;
    private final InstructionService instructionService;

    private RTopic privateMessagingChannel;
    private final boolean loaded;

    public RedisMessaging(@Nonnull VCorePlugin<?, ?> plugin, boolean clusterMode, @Nonnull String[] addressArray, String redisPassword) {
        super(plugin, clusterMode, addressArray, redisPassword);
        globalMessagingChannel = redissonClient.getTopic("VCoreMessagingChannel", new SerializationCodec());

        this.messageListener = (channel, msg) -> {
            if (!(msg instanceof SimpleRedisMessage))
                return;
            // Own Messages won't throw an event
            if (isOwnMessage(msg))
                return;
            plugin.getServices().eventBus.post(new MessageEvent(channel.toString(), msg));
        };
        globalMessagingChannel.addListener(Message.class, messageListener);

        loaded = true;
        instructionService = new InstructionService(plugin);
    }

    @Override
    public void setupPrivateMessagingChannel() {
        privateMessagingChannel = getServerMessagingChannel(plugin.getCoreInstance().getServerName());
        privateMessagingChannel.addListener(Message.class, messageListener);
    }

    private RTopic getServerMessagingChannel(String serverName) {
        return redissonClient.getTopic("ServerMessagingChannel_" + serverName.toLowerCase(), new SerializationCodec());
    }

    @Override
    public RedisMessageBuilder constructMessage() {
        return new RedisMessageBuilder(getSessionUUID(), getSenderName());
    }

    @Override
    public void publishMessage(Message message) {
        globalMessagingChannel.publish(message);
    }

    @Override
    public void sendMessage(Message message, String... serverNames) {
        if (serverNames == null || serverNames.length == 0)
            return;
        for (String serverName : serverNames) {
            if (serverName.equals(plugin.getCoreInstance().getServerName()))
                continue;
            getServerMessagingChannel(serverName).publish(message);
        }
    }

    @Override
    public boolean isOwnMessage(Message message) {
        return message.getSender().equals(getSessionUUID());
    }

    @Override
    public String getSenderName() {
        return plugin.getCoreInstance().getServerName() + "_" + plugin.getPluginName();
    }

    @Override
    public InstructionService getInstructionService() {
        return instructionService;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void shutdown() {
        plugin.consoleMessage("&eShutting down Redis Messenger", false);
        globalMessagingChannel.removeListener(messageListener);
        plugin.consoleMessage("&eRedis Messenger shut down successfully", false);
    }
}
