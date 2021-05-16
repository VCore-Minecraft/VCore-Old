package de.verdox.vcorepaper.custom.nbtholders;

import de.tr7zw.changeme.nbtapi.NBTEntity;
import org.bukkit.entity.Entity;

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
    public <T> T getObject(String key, Class<T> type) {
        return nbtEntity.getObject(key,type);
    }

    @Override
    public void setObject(String key, Object value) {
        nbtEntity.setObject(key,value);
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
