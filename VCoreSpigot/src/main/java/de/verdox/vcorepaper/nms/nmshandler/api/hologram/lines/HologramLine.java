/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms.nmshandler.api.hologram.lines;

import de.verdox.vcorepaper.nms.nmshandler.api.hologram.VHologram;
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
