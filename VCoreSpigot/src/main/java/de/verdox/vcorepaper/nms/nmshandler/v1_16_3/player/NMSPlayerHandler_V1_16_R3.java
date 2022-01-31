/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms.nmshandler.v1_16_3.player;

import de.verdox.vcore.nms.nmshandler.api.player.NMSPlayerHandler;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 16.09.2021 18:26
 */
public class NMSPlayerHandler_V1_16_R3 implements NMSPlayerHandler {
    @Override
    public boolean silentlyGrantAdvancementProgress(@NotNull Player player, @NotNull Advancement advancement, @NotNull String criterionName) {
        return false;
    }
}
