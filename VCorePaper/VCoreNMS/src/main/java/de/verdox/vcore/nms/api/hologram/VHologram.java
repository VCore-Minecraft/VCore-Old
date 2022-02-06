/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nms.api.hologram;

import de.verdox.vcore.nms.api.hologram.lines.HologramLine;
import de.verdox.vcore.nms.api.hologram.lines.ItemLine;
import de.verdox.vcore.nms.api.hologram.lines.TextLine;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 23.06.2021 02:43
 */
public interface VHologram {

    TextLine setTextLine(String line);

    ItemLine setItemLine(ItemStack stack);

    HologramLine getLine(int index);

    void removeLine(int index);

    void clearLines();

    int size();

    void teleport(Location location);

    Location getLocation();

    HologramPacketManager getHologramPacketManager();

    long getCreationTimeStamp();

    void delete();

    boolean isDeleted();
}
