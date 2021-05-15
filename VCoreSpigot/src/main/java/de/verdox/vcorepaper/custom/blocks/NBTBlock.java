package de.verdox.vcorepaper.custom.blocks;

import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.verdox.vcorepaper.VCorePaper;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NBTBlock extends NBTContainer {

    private final BlockState blockState;
    private final VBlockManager vBlockManager;
    private long lastUse = System.currentTimeMillis();
    private final BlockPersistentData blockPersistentData;

    public NBTBlock(BlockState blockState, BlockPersistentData blockPersistentData){
        this.blockState = blockState;
        if(blockState == null)
            throw new NullPointerException("blockState can't be null!");
        vBlockManager = VCorePaper.getInstance().getVBlockManager();
        this.blockPersistentData = blockPersistentData;
    }

    public BlockState getBlockState() {
        return blockState;
    }

    @Override
    protected void saveCompound() {
        super.saveCompound();
        lastUse = System.currentTimeMillis();
        vBlockManager.saveBlockPersistentData(blockState);
    }

    private void saveMetaDataKey(String key, Object value){
        blockState.setMetadata(key, new FixedMetadataValue(VCorePaper.getInstance(), value));

        blockPersistentData.getJsonObject().put(key,value);
        lastUse = System.currentTimeMillis();
    }

    private Object getMetaDataKey(String key){
        lastUse = System.currentTimeMillis();

        Object dataFromJson = blockPersistentData.getJsonObject().get(key);
        if(!blockState.hasMetadata(key))
            return dataFromJson;
        Object dataFromBlockState = blockState.getMetadata(key);
        if(!dataFromJson.equals(dataFromBlockState))
            return dataFromBlockState;
        return blockState.getMetadata(key);
    }

    @Override
    public Set<String> getKeys() {
        Set<String> keys = new HashSet<>();
        blockPersistentData.getJsonObject().keySet().forEach(o -> keys.add(o.toString()));
        return keys;
    }

    @Override
    public Boolean getBoolean(String key) {
        try{
            getReadLock().lock();
            return (Boolean) getMetaDataKey(key);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public Integer getInteger(String key) {
        try{
            getReadLock().lock();
            return (Integer) getMetaDataKey(key);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public Byte getByte(String key) {
        try{
            getReadLock().lock();
            return (Byte) getMetaDataKey(key);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public byte[] getByteArray(String key) {
        try{
            getReadLock().lock();
            return (byte[]) getMetaDataKey(key);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public Double getDouble(String key) {
        try{
            getReadLock().lock();
            return (Double) getMetaDataKey(key);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public Float getFloat(String key) {
        try{
            getReadLock().lock();
            return (Float) getMetaDataKey(key);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public int[] getIntArray(String key) {
        try{
            getReadLock().lock();
            return (int[]) getMetaDataKey(key);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public ItemStack getItemStack(String key) {
        try{
            getReadLock().lock();
            return (ItemStack) getMetaDataKey(key);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public Long getLong(String key) {
        try{
            getReadLock().lock();
            return (Long) getMetaDataKey(key);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public <T> T getObject(String key, Class<T> type) {
        try{
            getReadLock().lock();
            return (T) getMetaDataKey(key);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public String getString(String key) {
        try{
            getReadLock().lock();
            return (String) getMetaDataKey(key);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public UUID getUUID(String key) {
        try{
            getReadLock().lock();
            return (UUID) getMetaDataKey(key);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public void setObject(String key, Object value) {
        try {
            getWriteLock().lock();
            saveMetaDataKey(key,value);
            saveCompound();
        } finally {
            getWriteLock().unlock();
        }
    }

    @Override
    public void setBoolean(String key, Boolean value) {
        try {
            getWriteLock().lock();
            saveMetaDataKey(key,value);
            saveCompound();
        } finally {
            getWriteLock().unlock();
        }
    }

    @Override
    public void setByte(String key, Byte value) {
        try {
            getWriteLock().lock();
            saveMetaDataKey(key,value);
            saveCompound();
        } finally {
            getWriteLock().unlock();
        }
    }

    @Override
    public void setByteArray(String key, byte[] value) {
        try {
            getWriteLock().lock();
            saveMetaDataKey(key,value);
            saveCompound();
        } finally {
            getWriteLock().unlock();
        }
    }

    @Override
    public void setDouble(String key, Double value) {
        try {
            getWriteLock().lock();
            saveMetaDataKey(key,value);
            saveCompound();
        } finally {
            getWriteLock().unlock();
        }
    }

    @Override
    public void setFloat(String key, Float value) {
        try {
            getWriteLock().lock();
            saveMetaDataKey(key,value);
            saveCompound();
        } finally {
            getWriteLock().lock();
        }
    }

    @Override
    public void setIntArray(String key, int[] value) {
        try {
            getWriteLock().lock();
            saveMetaDataKey(key,value);
            saveCompound();
        } finally {
            getWriteLock().lock();
        }
    }

    @Override
    public void setInteger(String key, Integer value) {
        try {
            getWriteLock().lock();
            saveMetaDataKey(key,value);
            saveCompound();
        } finally {
            getWriteLock().unlock();
        }
    }

    @Override
    public void setItemStack(String key, ItemStack item) {
        try {
            getWriteLock().lock();
            saveMetaDataKey(key,item);
        } finally {
            getWriteLock().unlock();
        }
    }

    @Override
    public void setLong(String key, Long value) {
        try {
            getWriteLock().lock();
            saveMetaDataKey(key,value);
            saveCompound();
        } finally {
            getWriteLock().unlock();
        }
    }

    @Override
    public void setShort(String key, Short value) {
        try {
            getWriteLock().lock();
            saveMetaDataKey(key,value);
            saveCompound();
        } finally {
            getWriteLock().unlock();
        }
    }

    @Override
    public void setString(String key, String value) {
        try {
            getWriteLock().lock();
            saveMetaDataKey(key,value);
            saveCompound();
        } finally {
            getWriteLock().unlock();
        }
    }

    @Override
    public void setUUID(String key, UUID value) {
        try {
            getWriteLock().lock();
            saveMetaDataKey(key,value);
            saveCompound();
        } finally {
            getWriteLock().unlock();
        }
    }
}
