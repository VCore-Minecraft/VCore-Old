/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbtholders.item;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.verdox.vcorepaper.custom.nbtholders.NBTHolder;
import de.verdox.vcorepaper.custom.nbtholders.NBTHolderImpl;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NBTItemHolder extends NBTHolderImpl<ItemStack> {
    private final boolean directApply;

    public NBTItemHolder(ItemStack stack, boolean directApply) {
        super(stack);
        if (stack == null || stack.getType().equals(Material.AIR))
            throw new NullPointerException("Stack can't be null/Air!");
        this.directApply = directApply;
    }

    public NBTItemHolder(ItemStack stack) {
        this(stack, false);
    }

    @Override
    protected NBTCompound getNbtCompound() {
        return new NBTItem(dataHolder, directApply);
    }

    @NotNull
    @Override
    public NBTHolder getVanillaCompound() {
        return this;
    }
}
