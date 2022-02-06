/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nms.api.player;

import de.verdox.vcore.nms.NMSHandler;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 16.09.2021 18:25
 */
public interface NMSPlayerHandler extends NMSHandler {

    /**
     * Silently grants Criteria for an advancement without triggering Bukkit Events
     *
     * @return The Result of this operation
     */
    boolean silentlyGrantAdvancementProgress(@NotNull Player player, @NotNull Advancement advancement, @NotNull String criterionName);
}
