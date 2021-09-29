/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.util.bukkit;

import de.verdox.vcore.plugin.wrapper.types.StackPile;
import de.verdox.vcore.util.VCoreUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.08.2021 22:15
 */
public class BukkitInventoryUtil {
    public boolean hasStorageContentSpaceFor(@NotNull Inventory inventory, @NotNull StackPile... piles) {
        Inventory copy = Bukkit.createInventory(null, (inventory.getSize() / 9) * 9, "");
        copyStorageContentsToInventory(inventory, copy);
        for (StackPile pile : piles) {
            for (ItemStack itemStack : pile.splitIntoVanillaStacks()) {
                if (itemStack == null || itemStack.getType().isAir())
                    continue;
                Map<Integer, ItemStack> leftItems = copy.addItem(itemStack);
                if (!leftItems.isEmpty())
                    return false;
            }
        }
        return true;
    }

    public boolean hasStorageContentSpaceFor(@NotNull Inventory inventory, @NotNull ItemStack... stacks) {
        Inventory copy = Bukkit.createInventory(null, (inventory.getSize() / 9) * 9, "");
        copyStorageContentsToInventory(inventory, copy);
        for (ItemStack itemStack : stacks) {
            if (itemStack == null || itemStack.getType().isAir())
                continue;
            Map<Integer, ItemStack> leftItems = copy.addItem(itemStack);
            if (!leftItems.isEmpty())
                return false;
        }
        return true;
    }

    public int countItemAmount(@NotNull Inventory inventory, @NotNull ItemStack itemStack) {
        return inventory.all(itemStack.getType()).values()
                .stream()
                .filter(stack -> stack.isSimilar(itemStack)).flatMapToInt(stack -> IntStream.of(stack.getAmount())).sum();
    }

    public boolean containsAtLeast(@NotNull Inventory inventory, @NotNull Inventory inventoryWithItems) {
        for (ItemStack stack : inventoryWithItems) {
            if (stack == null || stack.getType().isAir())
                continue;
            int amountAtLeast = VCoreUtil.BukkitUtil.getBukkitInventoryUtil().countItemAmount(inventoryWithItems, stack);
            if (!inventory.containsAtLeast(stack, amountAtLeast))
                return false;
        }
        return true;
    }

    public Map<ItemStack, Integer> countItems(@NotNull Inventory inventory) {
        Map<ItemStack, Integer> counted = new LinkedHashMap<>();
        for (ItemStack storageContent : inventory.getStorageContents()) {
            if (storageContent == null || storageContent.getType().isAir())
                continue;
            if (!counted.containsKey(storageContent))
                counted.put(storageContent.asQuantity(1), storageContent.getAmount());
            else {
                counted.put(storageContent.asQuantity(1), storageContent.getAmount() + counted.get(storageContent));
            }
        }
        return counted;
    }

    public Map<Integer, ItemStack> removeItem(@NotNull Inventory inventory, @NotNull ItemStack itemStack, @Positive int number) {
        return inventory.removeItem(new StackPile(itemStack, number).splitIntoVanillaStacks());
    }

    public Map<Integer, ItemStack> addItem(@NotNull Inventory inventory, @NotNull ItemStack itemStack, @Positive int number) {
        return inventory.addItem(new StackPile(itemStack, number).splitIntoVanillaStacks());
    }

    public Inventory cloneInventory(@NotNull Inventory inventory, @NotNull String title) {
        Inventory copy;
        if (inventory.getType().equals(InventoryType.CHEST))
            copy = Bukkit.createInventory(null, inventory.getSize(), ChatColor.translateAlternateColorCodes('&', title));
        else
            copy = Bukkit.createInventory(null, inventory.getType(), ChatColor.translateAlternateColorCodes('&', title));
        copy.setContents(inventory.getContents());
        return copy;
    }

    public void copyStorageContentsToInventory(@NotNull Inventory inventory, @NotNull Inventory copyTo) {
        if (inventory.getSize() <= copyTo.getSize())
            copyTo.setStorageContents(inventory.getStorageContents());
        else
            for (int i = 0; i < inventory.getStorageContents().length; i++) {
                if (i >= inventory.getSize())
                    break;
                copyTo.setItem(i, inventory.getStorageContents()[i]);
            }
    }
}
