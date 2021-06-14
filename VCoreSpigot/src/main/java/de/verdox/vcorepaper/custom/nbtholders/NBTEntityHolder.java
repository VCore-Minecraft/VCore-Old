package de.verdox.vcorepaper.custom.nbtholders;

import de.tr7zw.changeme.nbtapi.NBTEntity;
import de.verdox.vcore.util.VCoreUtil;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.UUID;

public class NBTEntityHolder implements NBTHolder {

    private final NBTEntity nbtEntity;

    public NBTEntityHolder(Entity entity){
        this.nbtEntity = new NBTEntity(entity);
    }

    @Override
    public Boolean getBoolean(String key) {
        return nbtEntity.getBoolean(key);
    }

    @Override
    public Integer getInteger(String key) {
        return nbtEntity.getInteger(key);
    }

    @Override
    public Double getDouble(String key) {
        return nbtEntity.getDouble(key);
    }

    @Override
    public String getString(String key) {
        return nbtEntity.getString(key);
    }

    @Override
    public UUID getUUID(String key) {
        return nbtEntity.getUUID(key);
    }

    @Override
    public Long getLong(String key) {
        return nbtEntity.getLong(key);
    }

    @Override
    public ItemStack getItemStack(String key) {
        return nbtEntity.getItemStack(key);
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
            return (T) nbtEntity.getItemStack(key);
        return nbtEntity.getObject(key,type);
    }

    @Override
    public void setObject(String key, Object value) {
        if(value instanceof Boolean)
            nbtEntity.setBoolean(key, (Boolean) value);
        else if(value instanceof Integer)
            nbtEntity.setInteger(key, (Integer) value);
        else if(value instanceof Double)
            nbtEntity.setDouble(key, (Double) value);
        else if(value instanceof String)
            nbtEntity.setString(key, (String) value);
        else if(value instanceof UUID)
            nbtEntity.setUUID(key, (UUID) value);
        else if(value instanceof Long)
            nbtEntity.setLong(key, (Long) value);
        else if(value instanceof ItemStack)
            nbtEntity.setItemStack(key, (ItemStack) value);
        else
            nbtEntity.setObject(key, value);
    }

    @Override
    public Set<String> getKeys() {
        return nbtEntity.getKeys();
    }

    @Override
    public boolean hasKey(String key) {
        return nbtEntity.hasKey(key);
    }

    @Override
    public void removeKey(String key) {
        nbtEntity.removeKey(key);
    }

    @Override
    public void save() {
        //NBTEntity saves internally
    }
}
