/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.util;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.08.2021 19:55
 */
public class AdvancementsUtil {
    public static AdvancementProgress getProgress(@NotNull Player player, @NotNull Advancement advancement) {
        return player.getAdvancementProgress(Objects.requireNonNull(Bukkit.getAdvancement(advancement.getKey())));
    }

    public static AdvancementProgress getProgress(@NotNull Player player, @NotNull NamespacedKey namespacedKey) {
        return player.getAdvancementProgress(Objects.requireNonNull(Bukkit.getAdvancement(namespacedKey)));
    }

    public static Advancement toBukkitAdvancement(@NotNull Player player, @NotNull net.roxeez.advancement.Advancement customAdvancement) {
        return Bukkit.getAdvancement(customAdvancement.getKey());
    }

    public static void regrantAdvancement(@NotNull Player player, @NotNull Advancement advancement) {
        clearAdvancement(player, advancement);
        regrantAdvancement(player, advancement);
    }

    public static boolean awardAdvancement(@NotNull Player player, @NotNull Advancement advancement) {
        AdvancementProgress advancementProgress = getProgress(player, advancement);
        if (advancementProgress.isDone())
            return false;
        for (String criterion : advancement.getCriteria())
            advancementProgress.awardCriteria(criterion);
        for (String criterion : advancement.getCriteria())
            player.sendMessage(criterion);
        return true;
    }

    public static boolean clearAdvancement(@NotNull Player player, @NotNull Advancement advancement) {
        AdvancementProgress advancementProgress = getProgress(player, advancement);
        for (String criterion : advancement.getCriteria())
            advancementProgress.revokeCriteria(criterion);
        return true;
    }
}
