/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Serializer {

    public static String itemStackToBase64(ItemStack stack) {
        if (stack == null)
            throw new NullPointerException("Stack can't be null");
        if (stack.getType().isAir())
            throw new NullPointerException("Stack can't be air");
        return itemStackArrayToBase64(new ItemStack[]{stack});
    }

    public static ItemStack Base64ToItemStack(String base64) {
        if (base64 == null)
            return null;
        try {
            return itemStackArrayFromBase64(base64)[0];
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
        if (items == null)
            return null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(items.length);

            for (ItemStack item : items) {
                if (item != null) {
                    dataOutput.writeObject(item.serializeAsBytes());
                } else {
                    dataOutput.writeObject(null);
                }
            }

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    /**
     * Gets an array of ItemStacks from Base64 string.
     *
     * @param data Base64 string to convert to ItemStack array.
     * @return ItemStack array created from the Base64 string.
     * @throws IOException
     */
    public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
        if (data == null)
            return null;
        Player player;
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            for (int Index = 0; Index < items.length; Index++) {
                byte[] stack = (byte[]) dataInput.readObject();

                if (stack != null) {
                    items[Index] = ItemStack.deserializeBytes(stack);
                } else {
                    items[Index] = null;
                }
            }

            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

}
