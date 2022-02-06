/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.nms.serializable;

import de.verdox.vcore.nms.VCoreNMSModule;
import de.verdox.vcore.synchronization.pipeline.datatypes.CustomPipelineData;
import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.collections.MapBsonReference;
import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.objects.StringBsonReference;
import de.verdox.vcorepaper.VCorePaper;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 16.09.2021 17:00
 */
public class SerializableAdvancementProgress implements Serializable, CustomPipelineData {

    protected final Map<String, Object> data;

    public SerializableAdvancementProgress(@NotNull String id, Set<AdvancementProgress> progressSet) {
        this.data = new ConcurrentHashMap<>();
        saveAdvancements(progressSet);
        new StringBsonReference(data, "id").setValue(id);
    }

    public SerializableAdvancementProgress(@NotNull Map<String, Object> data) {
        this.data = data;
    }

    public SerializableAdvancementProgress() {
        this.data = new HashMap<>();
    }

    public void saveAdvancements(Set<AdvancementProgress> progressSet) {
        MapBsonReference<String, Map<String, Boolean>> mapReference = new MapBsonReference<>(data, "advancementProgress");
        Map<String, Map<String, Boolean>> progress = mapReference.orElse(new HashMap<>());

        progressSet.forEach(advancementProgress -> {
            Map<String, Boolean> criteria = new HashMap<>();
            advancementProgress.getAwardedCriteria().forEach(s -> criteria.put(s, true));
            advancementProgress.getRemainingCriteria().forEach(s -> criteria.put(s, false));

            progress.put(advancementProgress.getAdvancement().getKey().asString(), criteria);
        });

        mapReference.setValue(progress);
    }

    public void restoreData(@NotNull Player player, @Nullable Runnable callback) {
        Map<String, Map<String, Boolean>> progress = new MapBsonReference<String, Map<String, Boolean>>(data, "advancementProgress").orElse(null);

        progress.forEach((advancementKey, progressMap) -> {
            String[] split = advancementKey.split(":");
            NamespacedKey namespacedKey = new NamespacedKey(split[0], split[1]);
            Advancement advancement = Bukkit.getAdvancement(namespacedKey);
            if (advancement == null)
                return;
            AdvancementProgress advancementProgress = player.getAdvancementProgress(advancement);
            progressMap.forEach((criteria, unlocked) -> {
                VCorePaper.getInstance().sync(() -> {
                    if (unlocked)
                        VCorePaper.getInstance().getModuleLoader().getModule(VCoreNMSModule.class).getNmsManager().getNMSPlayerHandler().silentlyGrantAdvancementProgress(player, advancement, criteria);
                    else
                        advancementProgress.revokeCriteria(criteria);
                });
            });
        });
        if (callback != null)
            callback.run();
    }

    @Override
    public Map<String, Object> getUnderlyingMap() {
        return data;
    }

    @Override
    public void save() {

    }
}
