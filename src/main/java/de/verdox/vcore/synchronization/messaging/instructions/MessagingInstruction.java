/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging.instructions;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.wrapper.PlatformWrapper;
import de.verdox.vcore.plugin.wrapper.proxy.ProxyPlatform;
import de.verdox.vcore.plugin.wrapper.gameserver.GameServerPlatform;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.08.2021 21:33
 */
public abstract class MessagingInstruction<T> implements InstructionSender<T> {
    protected final String[] parameters;
    protected final List<Class<?>> types;
    protected final UUID uuid;
    private final Long creationTimeStamp = System.currentTimeMillis();
    protected VCorePlugin<?, ?> plugin;
    protected GameServerPlatform gameServerPlatform;
    protected ProxyPlatform proxyPlatform;
    protected PlatformWrapper platformWrapper;
    private Object[] data;


    public MessagingInstruction(@NotNull UUID uuid) {
        Objects.requireNonNull(uuid, "uuid can't be null!");
        this.uuid = uuid;
        this.parameters = parameters().toArray(String[]::new);
        this.types = dataTypes();
    }

    public void setPlugin(VCorePlugin<?, ?> plugin) {
        this.plugin = plugin;
        this.platformWrapper = plugin.getPlatformWrapper();
        this.gameServerPlatform = plugin.getPlatformWrapper().getGameServerPlatform();
        this.proxyPlatform = plugin.getPlatformWrapper().getProxyPlatform();
    }

    public MessagingInstruction<T> withData(Object... data) {
        if (data.length != types.size())
            throw new IllegalStateException("Wrong Input Parameter Length for " + getClass().getSimpleName() + " [" + dataTypes().size() + "]");
        for (int i = 0; i < types.size(); i++) {
            Class<?> type = types.get(i);
            Object datum = data[i];
            if (!type.isAssignableFrom(datum.getClass()))
                throw new IllegalStateException(datum + " is not type or subtype of " + type.getName());

        }
        this.data = data;
        return this;
    }

    public boolean checkOnlineOnSpigot(@NotNull UUID playerUUID) {
        if (gameServerPlatform == null)
            return false;
        return platformWrapper.isPlayerOnline(playerUUID);
    }

    public boolean checkOnlineOnBungeeCord(@NotNull UUID playerUUID) {
        if (proxyPlatform == null)
            return false;
        return platformWrapper.isPlayerOnline(playerUUID);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String[] getParameters() {
        return parameters;
    }

    public Object[] getData() {
        return data;
    }

    public Long getCreationTimeStamp() {
        return creationTimeStamp;
    }

    protected abstract List<Class<?>> dataTypes();

    protected abstract List<String> parameters();

    @Override
    public String toString() {
        return "MessagingInstruction{" +
                "parameters=" + Arrays.toString(parameters) +
                ", types=" + types +
                ", uuid=" + uuid +
                ", creationTimeStamp=" + creationTimeStamp +
                ", plugin=" + plugin +
                ", spigotPlatform=" + gameServerPlatform +
                ", bungeePlatform=" + proxyPlatform +
                ", platformWrapper=" + platformWrapper +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
