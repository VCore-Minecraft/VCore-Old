/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.messaging.instructions;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.wrapper.bungeecord.BungeePlatform;
import de.verdox.vcore.plugin.wrapper.spigot.SpigotPlatform;

import java.util.List;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.08.2021 21:33
 */
public abstract class MessagingInstruction {
    protected VCorePlugin<?,?> plugin;
    protected SpigotPlatform spigotPlatform;
    protected BungeePlatform bungeePlatform;
    protected final String[] parameters;
    protected final List<Class<?>> types;
    protected final UUID uuid;
    private final Long creationTimeStamp = System.currentTimeMillis();
    private Object[] data;


    public MessagingInstruction(UUID uuid){
        this.uuid = uuid;
        this.parameters = parameters().toArray(String[]::new);
        this.types = dataTypes();
    }
    public void setPlugin(VCorePlugin<?, ?> plugin) {
        this.plugin = plugin;
        this.spigotPlatform = plugin.getPlatformWrapper().getSpigotPlatform();
        this.bungeePlatform = plugin.getPlatformWrapper().getBungeePlatform();
    }

    public MessagingInstruction withData(Object... data){
        if(data.length != types.size())
            throw new IllegalStateException("Wrong Input Parameter Length for "+getClass().getSimpleName()+" ["+dataTypes().size()+"]");
        for (int i = 0; i < types.size(); i++) {
            Class<?> type = types.get(i);
            Object datum = data[i];
            if(!type.isAssignableFrom(datum.getClass()))
                throw new IllegalStateException(datum+" is not type or subtype of "+type.getName());
        }
        this.data = data;
        return this;
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

    public abstract boolean onSend(Object[] instructionData);
}