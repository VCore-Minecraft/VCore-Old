/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbtholders.item;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.verdox.vcorepaper.custom.nbtholders.NBTHolderImpl;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NBTItemHolder extends NBTHolderImpl<ItemStack, NBTItem> {
    private final boolean directApply;
    private final NBTItem nbtItem;

    public NBTItemHolder(ItemStack stack, boolean directApply) {
        super(stack);
        if (stack == null || stack.getType().equals(Material.AIR))
            throw new NullPointerException("Stack can't be null/Air!");
        this.directApply = directApply;
        this.nbtItem = new NBTItem(stack, directApply);
    }

    public NBTItemHolder(ItemStack stack) {
        this(stack, false);
    }

    @Override
    public NBTItem getPersistentDataContainer() {
        return nbtItem;
    }

    @Override
    public NBTCompound getVanillaCompound() {
        return nbtItem;
    }
}
