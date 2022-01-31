/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nms.nmshandler.api.hologram;

import org.bukkit.entity.Player;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 23.06.2021 02:44
 */
public interface HologramPacketManager {
    VHologram getHologram();

    boolean isGlobal();

    void setGlobal(boolean value);

    void showTo(Player player);

    void hideFrom(Player player);

    boolean isVisibleTo(Player player);
}
