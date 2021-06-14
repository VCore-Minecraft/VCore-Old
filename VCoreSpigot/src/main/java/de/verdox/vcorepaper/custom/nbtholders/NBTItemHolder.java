package de.verdox.vcorepaper.custom.nbtholders;

import de.tr7zw.changeme.nbtapi.NBTItem;
import de.verdox.vcore.util.VCoreUtil;
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
    public ItemStack getItemStack(String key) {
        return nbtItem.getItemStack(key);
    }

    @Override
    public <T> T getObject(String key, Class<T> type) {
        if(type.equals(Boolean.class))
            return (T) getBoolean(key);
        else if(type.equals(Integer.class))
            return (T) getInteger(key);
        else if(type.equals(Double.class))
            return (T) getDouble(key);
        else if(type.equals(String.class))
            return (T) getString(key);
        else if(type.equals(UUID.class))
            return (T) getUUID(key);
        else if(type.equals(Long.class))
            return (T) getLong(key);
        else if(type.equals(ItemStack.class))
            return (T) nbtItem.getItemStack(key);
        return nbtItem.getObject(key,type);
    }

    @Override
    public void setObject(String key, Object value) {
        if(value instanceof Boolean)
            nbtItem.setBoolean(key, (Boolean) value);
        else if(value instanceof Integer)
            nbtItem.setInteger(key, (Integer) value);
        else if(value instanceof Double)
            nbtItem.setDouble(key, (Double) value);
        else if(value instanceof String)
            nbtItem.setString(key, (String) value);
        else if(value instanceof UUID)
            nbtItem.setUUID(key, (UUID) value);
        else if(value instanceof Long)
            nbtItem.setLong(key, (Long) value);
        else if(value instanceof ItemStack)
            nbtItem.setItemStack(key, (ItemStack) value);
        else
            nbtItem.setObject(key, value);
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
