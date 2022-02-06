/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.workernpc;

import de.verdox.vcore.nbt.entities.CustomEntityManager;
import de.verdox.vcore.workernpc.professions.VanillaTrader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.09.2021 21:32
 */
public class ProfessionRegistry {
    private final Map<String, Class<? extends NPCProfession>> registry = new HashMap<>();
    private final CustomEntityManager customEntityManager;

    public ProfessionRegistry(@NotNull CustomEntityManager customEntityManager) {
        this.customEntityManager = customEntityManager;
        registerProfession("vanillaTrader", VanillaTrader.class);
    }

    @Nullable
    public String getID(Class<? extends NPCProfession> type) {
        return registry.entrySet().stream().filter(stringClassEntry -> stringClassEntry.getValue().equals(type)).map(Map.Entry::getKey).findAny().orElse(null);
    }

    public void registerProfession(String identifier, Class<? extends NPCProfession> type) {
        if (registry.containsKey(identifier))
            throw new IllegalStateException("Identifier already registered in ProfessionRegistry: " + identifier);
        customEntityManager.getVCorePlugin().consoleMessage("&eRegistering Profession " + type.getName(), false);
        registry.put(identifier, type);
    }

    public Class<? extends NPCProfession> getProfessionClass(@NotNull String identifier) {
        return registry.get(identifier);
    }

    public Set<String> getProfessions() {
        return registry.keySet();
    }
}
