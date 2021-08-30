/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.advancement;

import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.util.AdvancementsUtil;
import net.roxeez.advancement.AdvancementCreator;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 29.08.2021 13:48
 */
public abstract class VAdvancement implements AdvancementCreator {
    public abstract NamespacedKey getNamespacedKey();

    public Advancement asBukkitAdvancement() {
        Advancement advancement = Bukkit.getAdvancement(getNamespacedKey());
        if (advancement == null)
            throw new IllegalStateException("Advancements need to be registered in VCorePaper AdvancementManager first!");
        return Bukkit.getAdvancement(getNamespacedKey());
    }

    public void awardPlayers(@NotNull Player... player) {
        for (Player player1 : player)
            awardPlayer(player1);
    }

    public void awardPlayer(@NotNull Player player) {
        VCorePaper.getInstance().sync(() -> AdvancementsUtil.regrantAdvancement(player, asBukkitAdvancement()));
    }
}
