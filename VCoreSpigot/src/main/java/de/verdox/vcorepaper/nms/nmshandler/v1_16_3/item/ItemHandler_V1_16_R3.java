/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms.nmshandler.v1_16_3.item;

import de.verdox.vcorepaper.nms.nmshandler.api.item.NMSItemHandler;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 02.09.2021 19:11
 */
public class ItemHandler_V1_16_R3 implements NMSItemHandler {

    @Override
    public void changeMaxStackSize(@NotNull ItemStack stack, @Positive int newStackSize) {
        CraftItemStack craftItemStack = (CraftItemStack) stack;
        net.minecraft.server.v1_16_R3.ItemStack nmsStack = craftItemStack.getHandle();
        try {
            System.out.println(Arrays.toString(nmsStack.getItem().getClass().getFields()));
            Field stackSize = nmsStack.getItem().getClass().getDeclaredField("maxStackSize");
            stackSize.setAccessible(true);
            stackSize.setInt(nmsStack.getItem(), newStackSize);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
