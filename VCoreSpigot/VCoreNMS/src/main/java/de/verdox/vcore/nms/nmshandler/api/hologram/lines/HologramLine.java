/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nms.nmshandler.api.hologram.lines;

import de.verdox.vcore.nms.nmshandler.api.hologram.VHologram;
import org.bukkit.Location;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 23.06.2021 02:40
 */
public interface HologramLine {
    VHologram getHologram();

    Location getLocation();

    boolean isEmpty();
}
