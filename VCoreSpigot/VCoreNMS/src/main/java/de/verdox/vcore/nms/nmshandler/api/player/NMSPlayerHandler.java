/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nms.nmshandler.api.player;

import de.verdox.vcore.nms.NMSHandler;
import de.verdox.vcore.nms.NMSVersion;
import de.verdox.vcore.nms.nmshandler.api.entity.NMSEntityHandler;
import de.verdox.vcore.nms.nmshandler.v1_16_3.player.NMSPlayerHandler_V1_16_R3;
import de.verdox.vcore.nms.nmshandler.v_1_17_1.player.NMSPlayerHandler_V1_17_1R1;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 16.09.2021 18:25
 */
public interface NMSPlayerHandler extends NMSHandler {
    static NMSPlayerHandler getRightHandler(NMSVersion nmsVersion) {
        if (nmsVersion.equals(NMSVersion.V1_16_5))
            return new NMSPlayerHandler_V1_16_R3();
        else if (nmsVersion.equals(NMSVersion.V1_17_1))
            return new NMSPlayerHandler_V1_17_1R1();
        throw new NotImplementedException("This Handler [" + NMSEntityHandler.class.getName() + "] is not implemented for NMS version: " + nmsVersion.getNmsVersionTag());
    }

    /**
     * Silently grants Criteria for an advancement without triggering Bukkit Events
     *
     * @return The Result of this operation
     */
    boolean silentlyGrantAdvancementProgress(@NotNull Player player, @NotNull Advancement advancement, @NotNull String criterionName);
}
