/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.bukkitplayerhandler.model;

import de.verdox.vcorepaper.custom.util.Serializer;
import org.bukkit.inventory.ItemStack;
import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RId;

import java.io.IOException;
import java.io.Serializable;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 20.06.2021 00:19
 */
public class SerializableInventory implements Serializable {

    private String id;
    private String storageContents;
    private String armorContents;

    public SerializableInventory(String id, ItemStack[] storageContents, ItemStack[] armorContents){
        this.id = id;
        this.storageContents = Serializer.itemStackArrayToBase64(storageContents);
        this.armorContents = Serializer.itemStackArrayToBase64(armorContents);
    }

    SerializableInventory(){}

    public ItemStack[] deSerializeStorageContents() throws IOException {
        return Serializer.itemStackArrayFromBase64(storageContents);
    }

    public ItemStack[] deSerializeArmorContents() throws IOException {
        return Serializer.itemStackArrayFromBase64(armorContents);
    }

    public String getId() {
        return id;
    }

    public String getArmorContents() {
        return armorContents;
    }

    public String getStorageContents() {
        return storageContents;
    }
}
