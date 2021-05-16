package de.verdox.vcorepaper.custom.nbtholders;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.UUID;

public class NBTItemHolder implements NBTHolder {

    private final NBTItem nbtItem;

    public NBTItemHolder(ItemStack stack, boolean directApply){
        if(stack == null || stack.getType().equals(Material.AIR))
            throw new NullPointerException("Stack can't be null/Air!");
        this.nbtItem = new NBTItem(stack, directApply);
    }

    public NBTItemHolder(ItemStack stack){
        this.nbtItem = new NBTItem(stack, false);
    }

    @Override
    public Boolean getBoolean(String key) {
        return nbtItem.getBoolean(key);
    }

    @Override
    public Integer getInteger(String key) {
        return nbtItem.getInteger(key);
    }

    @Override
    public Double getDouble(String key) {
        return nbtItem.getDouble(key);
    }

    @Override
    public String getString(String key) {
        return nbtItem.getString(key);
    }

    @Override
    public UUID getUUID(String key) {
        return nbtItem.getUUID(key);
    }

    @Override
    public Long getLong(String key) {
        return nbtItem.getLong(key);
    }

    @Override
    public <T> T getObject(String key, Class<T> type) {
        return nbtItem.getObject(key,type);
    }

    @Override
    public void setObject(String key, Object value) {
        nbtItem.setObject(key,value);
    }

    @Override
    public Set<String> getKeys() {
        return nbtItem.getKeys();
    }

    @Override
    public boolean hasKey(String key) {
        return nbtItem.hasKey(key);
    }

    @Override
    public void removeKey(String key) {
        nbtItem.removeKey(key);
    }

    @Override
    public void save() {
        // NBTItem saves internally
    }
}
