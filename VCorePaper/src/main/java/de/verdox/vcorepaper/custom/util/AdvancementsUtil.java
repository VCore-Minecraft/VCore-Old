/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.util;

import de.verdox.vcorepaper.VCorePaper;
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
        awardAdvancement(player, advancement, false);
    }

    public static boolean awardAdvancement(@NotNull Player player, @NotNull Advancement advancement) {
        return awardAdvancement(player, advancement, false);
    }

    public static boolean awardAdvancement(@NotNull Player player, @NotNull Advancement advancement, boolean silently) {
        //AdvancementProgress advancementProgress = getProgress(player, advancement);
        //if (advancementProgress.isDone())
        //    return false;
        //for (String criterion : advancement.getCriteria()) {
        //    if (silently)
        //        VCorePaper.getInstance().getNmsManager().getNMSPlayerHandler().silentlyGrantAdvancementProgress(player, advancement, criterion);
        //    else
        //        advancementProgress.awardCriteria(criterion);
        //}
        //return true;
        return false;
    }

    public static boolean clearAdvancement(@NotNull Player player, @NotNull Advancement advancement) {
        AdvancementProgress advancementProgress = getProgress(player, advancement);
        for (String criterion : advancement.getCriteria())
            advancementProgress.revokeCriteria(criterion);
        return true;
    }
}
