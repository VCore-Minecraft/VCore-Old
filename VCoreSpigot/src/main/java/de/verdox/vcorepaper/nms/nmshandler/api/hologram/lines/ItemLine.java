/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms.nmshandler.api.hologram.lines;

import org.bukkit.inventory.ItemStack;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 23.06.2021 02:41
 */
public interface ItemLine extends HologramLine {
    ItemStack getItemStack();

    void setItemStack(ItemStack stack);
}
