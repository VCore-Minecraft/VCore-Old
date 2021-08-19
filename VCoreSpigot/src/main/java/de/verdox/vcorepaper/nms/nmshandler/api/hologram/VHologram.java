/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms.nmshandler.api.hologram;

import de.verdox.vcorepaper.nms.NMSVersion;
import de.verdox.vcorepaper.nms.nmshandler.api.hologram.lines.HologramLine;
import de.verdox.vcorepaper.nms.nmshandler.api.hologram.lines.ItemLine;
import de.verdox.vcorepaper.nms.nmshandler.api.hologram.lines.TextLine;
import de.verdox.vcorepaper.nms.nmshandler.v1_16_3.hologram.VHologram_V1_16_R3;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 23.06.2021 02:43
 */
public interface VHologram {

    default VHologram createHologram(Location location) {
        NMSVersion nmsVersion = NMSVersion.findNMSVersion(Bukkit.getServer().getBukkitVersion());
        if (nmsVersion.equals(NMSVersion.V1_16_5)) {
            return new VHologram_V1_16_R3();
        }
        throw new NotImplementedException("This Handler [" + VHologram.class.getName() + "] is not implemented for NMS version: " + nmsVersion.getNmsVersionTag());
    }

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
