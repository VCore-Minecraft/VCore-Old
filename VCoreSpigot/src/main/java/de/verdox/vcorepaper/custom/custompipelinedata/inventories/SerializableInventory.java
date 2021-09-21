/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.custompipelinedata.inventories;

import de.verdox.vcore.synchronization.pipeline.datatypes.CustomPipelineData;
import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.objects.StringBsonReference;
import de.verdox.vcorepaper.custom.util.Serializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 19.08.2021 23:03
 */
public class SerializableInventory implements Serializable, CustomPipelineData {

    protected final Map<String, Object> data;
    private Inventory localRepresentation;

    public SerializableInventory(@NotNull String id, @NotNull ItemStack[] storageContents) {
        this.data = new ConcurrentHashMap<>();
        new StringBsonReference(data, "id").setValue(id);
        saveStorageContents(storageContents);
    }

    public SerializableInventory() {
        this(new ConcurrentHashMap<>());
    }

    public SerializableInventory(Map<String, Object> data) {
        this.data = data;
    }

    public final String getID() {
        return new StringBsonReference(data, "id").getValue();
    }

    public final void saveStorageContents(ItemStack[] storageContents) {
        new StringBsonReference(data, "storageContents").setValue(Serializer.itemStackArrayToBase64(storageContents));
    }

    public Inventory asInventory(String displayName, int size) {
        if (localRepresentation == null) {
            localRepresentation = Bukkit.createInventory(null, size, ChatColor.translateAlternateColorCodes('&', displayName));
            localRepresentation.setContents(deSerializeStorageContents());
        }
        return localRepresentation;
    }

    public void save() {
        if (localRepresentation != null)
            saveStorageContents(localRepresentation.getStorageContents());
    }

    public void saveToInventory(@NotNull Inventory inventory, boolean override) {
        if (override)
            inventory.clear();
        inventory.addItem(deSerializeStorageContents());
    }

    @NotNull
    public final ItemStack[] deSerializeStorageContents() {
        try {
            ItemStack[] storageContents = Serializer.itemStackArrayFromBase64(new StringBsonReference(data, "storageContents").orElse(null));
            if (storageContents != null)
                return storageContents;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ItemStack[0];
    }

    public void saveInventory(Inventory inventory) {
        saveStorageContents(inventory.getStorageContents());
    }

    @Override
    public final Map<String, Object> getUnderlyingMap() {
        return data;
    }

    @Override
    public String toString() {
        return "SerializableInventory{storageContents=" + Arrays.toString(deSerializeStorageContents()) + "}";
    }
}
