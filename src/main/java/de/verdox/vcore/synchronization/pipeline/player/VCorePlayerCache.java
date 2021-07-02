/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.player;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.messaging.event.MessageEvent;
import de.verdox.vcore.synchronization.messaging.messages.Message;
import de.verdox.vcore.synchronization.messaging.messages.MessageWrapper;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 15.06.2021 01:48
 */
public class VCorePlayerCache {

    //TODO: Nicht direkt löschen, sondern als löschbar markieren und nach 5 Minuten clearen oder sowas

    private final Map<UUID, VCorePlayer> cache = new ConcurrentHashMap<>();
    private final VCorePlugin<?, ?> vCorePlugin;

    public VCorePlayerCache(VCorePlugin<?,?> vCorePlugin){
        this.vCorePlugin = vCorePlugin;
        vCorePlugin.getServices().eventBus.register(new PlayerRedisListener());
    }

    public VCorePlayer getPlayer(UUID playerUUID){
        return cache.get(playerUUID);
    }

    public boolean isCached(UUID playerUUID){
        return cache.containsKey(playerUUID);
    }

    class PlayerRedisListener{
        @Subscribe
        public void playerMessages(MessageEvent e){
            MessageWrapper messageWrapper = new MessageWrapper(e.getMessage());
            if(!messageWrapper.validate(UUID.class, String.class))
                return;

            Message message = e.getMessage();

            UUID playerUUID = message.getData(0, UUID.class);
            String displayName = message.getData(1, String.class);

            if(messageWrapper.parameterContains("connection", "minecraft", "join")
                    || messageWrapper.parameterContains("connection", "bungee", "join")){

                if(messageWrapper.parameterContains("connection", "bungee", "join")){
                    vCorePlugin.consoleMessage("&eReceived Player Proxy Login&7: &b"+playerUUID+" &8[&a"+displayName+"&8]",false);
                }
                VCorePlayer vCorePlayer = new VCorePlayer(playerUUID, displayName);
                if(!cache.containsKey(vCorePlayer.getPlayerUUID())) {
                    cache.put(vCorePlayer.getPlayerUUID(), vCorePlayer);
                }
                else {
                    cache.get(playerUUID).setClearable(false);
                }
            }
            else if(messageWrapper.parameterContains("connection", "minecraft", "leave")
                    || messageWrapper.parameterContains("connection", "minecraft", "kick")
                    || messageWrapper.parameterContains("connection", "bungee", "leave")){

                if(messageWrapper.parameterContains("connection", "bungee", "leave")){
                    vCorePlugin.consoleMessage("&eReceived Player Proxy Logout&7: &b"+playerUUID+" &8[&a"+displayName+"&8]",false);
                    cache.remove(playerUUID);
                }
                else
                    if(cache.containsKey(playerUUID))
                        cache.get(playerUUID).setClearable(true);
            }
        }
    }
}
