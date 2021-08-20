/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.custompipelinedata.inventories;

import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.objects.StringBsonReference;
import de.verdox.vcorepaper.custom.util.Serializer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 19.08.2021 23:03
 */
public class SerializableInventory implements Serializable, CustomPipelineData {

    protected final Map<String, Object> data = new HashMap<>();

    public SerializableInventory(@Nonnull String id, @Nonnull ItemStack[] storageContents) {
        new StringBsonReference(data, "id").setValue(id);
        saveStorageContents(storageContents);
    }

    public SerializableInventory(Map<String, Object> data) {
        this.data.putAll(data);
    }

    public final String getID() {
        return new StringBsonReference(data, "id").getValue();
    }

    public final void saveStorageContents(ItemStack[] storageContents) {
        new StringBsonReference(data, "storageContents").setValue(Serializer.itemStackArrayToBase64(storageContents));
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
}
