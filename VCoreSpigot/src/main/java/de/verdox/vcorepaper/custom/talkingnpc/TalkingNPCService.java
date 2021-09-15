/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.talkingnpc;

import de.verdox.vcorepaper.custom.entities.CustomEntityManager;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 14.09.2021 15:18
 */
public class TalkingNPCService {
    private final Map<String, Class<? extends TalkingNPC>> talkingNPCTypes = new ConcurrentHashMap<>();
    private final CustomEntityManager customEntityManager;

    public TalkingNPCService(@NotNull CustomEntityManager customEntityManager) {
        this.customEntityManager = customEntityManager;
    }

    public void registerNPCType(@NotNull String identifier, @NotNull Class<? extends TalkingNPC> type) {
        talkingNPCTypes.put(identifier, type);
    }

    @NotNull
    public String getIdentifier(@NotNull Class<? extends TalkingNPC> type) {
        return talkingNPCTypes.entrySet().stream().filter(stringClassEntry -> stringClassEntry.getValue().equals(type)).map(Map.Entry::getKey).findAny().orElse("none");
    }

    public boolean isTalkingNPC(Entity entity) {
        TalkingNPC denizenNPC = customEntityManager.wrap(TalkingNPC.class, entity);
        return denizenNPC.verify();
    }

    @NotNull
    public TalkingNPC getTalkingNPC(Entity entity) {
        TalkingNPC denizenNPC = customEntityManager.wrap(TalkingNPC.class, entity);
        if (!denizenNPC.verify())
            return denizenNPC;
        String type = denizenNPC.getType();
        if (!talkingNPCTypes.containsKey(type))
            return denizenNPC;
        Class<? extends TalkingNPC> typeClass = talkingNPCTypes.get(type);
        return customEntityManager.wrap(typeClass, entity);
    }
}
