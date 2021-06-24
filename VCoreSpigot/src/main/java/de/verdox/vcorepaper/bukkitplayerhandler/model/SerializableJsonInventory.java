/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.bukkitplayerhandler.model;

import de.verdox.vcore.data.datatypes.serializables.VCoreSerializableJson;
import de.verdox.vcore.data.datatypes.serializables.references.objects.StringBsonReference;
import de.verdox.vcorepaper.custom.util.Serializer;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 20.06.2021 00:19
 */
public class SerializableJsonInventory extends VCoreSerializableJson {
    public SerializableJsonInventory(String id, ItemStack[] storageContents, ItemStack[] armorContents){
        new StringBsonReference(this, "id").setValue(id);
        new StringBsonReference(this, "armorContents").setValue(Serializer.itemStackArrayToBase64(armorContents));
        new StringBsonReference(this, "storageContents").setValue(Serializer.itemStackArrayToBase64(storageContents));
    }

    public ItemStack[] deSerializeStorageContents() throws IOException {
        return Serializer.itemStackArrayFromBase64(new StringBsonReference(this, "storageContents").orElse(null));
    }

    public ItemStack[] deSerializeArmorContents() throws IOException {
        return Serializer.itemStackArrayFromBase64(new StringBsonReference(this, "armorContents").orElse(null));
    }

    public String getID(){
        return new StringBsonReference(this, "storageContents").getValue();
    }
}
