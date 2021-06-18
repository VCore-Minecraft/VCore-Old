/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.player;

import com.google.common.eventbus.Subscribe;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.redisson.RedisResponseWrapper;
import de.verdox.vcore.redisson.events.RedisMessageEvent;
import de.verdox.vcore.redisson.messages.RedisSimpleMessage;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 15.06.2021 01:48
 */
public class VCorePlayerManager {

    //TODO: Nicht direkt löschen, sondern als löschbar markieren und nach 5 Minuten clearen oder sowas

    private final Map<UUID, VCorePlayer> cache = new ConcurrentHashMap<>();

    public VCorePlayerManager(VCorePlugin<?,?> vCorePlugin){
        vCorePlugin.getEventBus().register(new PlayerRedisListener());
    }

    public VCorePlayer getPlayer(UUID playerUUID){
        return cache.get(playerUUID);
    }

    public boolean isCached(UUID playerUUID){
        return cache.containsKey(playerUUID);
    }

    class PlayerRedisListener{
        @Subscribe
        public void playerMessages(RedisMessageEvent e){
            RedisResponseWrapper redisResponseWrapper = new RedisResponseWrapper(e, UUID.class, String.class);
            if(!redisResponseWrapper.validate())
                return;

            RedisSimpleMessage redisSimpleMessage = redisResponseWrapper.getRedisSimpleMessage();

            UUID playerUUID = redisSimpleMessage.getData(UUID.class, 0);
            String displayName = redisSimpleMessage.getData(String.class, 1);

            if(redisResponseWrapper.parameterContains("connection", "minecraft", "join")
                    || redisResponseWrapper.parameterContains("connection", "bungee", "join")){
                VCorePlayer vCorePlayer = new VCorePlayer(playerUUID, displayName);
                if(!cache.containsKey(vCorePlayer.getPlayerUUID()))
                    cache.put(vCorePlayer.getPlayerUUID(),vCorePlayer);
            }
            else if(redisResponseWrapper.parameterContains("connection", "minecraft", "leave")
                    || redisResponseWrapper.parameterContains("connection", "minecraft", "kick")
                    || redisResponseWrapper.parameterContains("connection", "bungee", "leave")){
                cache.remove(playerUUID);
            }
        }
    }
}
