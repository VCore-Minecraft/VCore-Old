package de.verdox.vcorepaper.custom.nbtholders;

import de.verdox.vcore.util.VCoreUtil;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.blocks.BlockPersistentData;
import de.verdox.vcorepaper.custom.blocks.CustomBlockManager;
import de.verdox.vcorepaper.custom.util.Serializer;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class NBTBlockHolder implements NBTHolder {

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    private final Location blockLocation;
    private final CustomBlockManager customBlockManager;
    private final BlockPersistentData blockPersistentData;
    private long lastUse = System.currentTimeMillis();

    public NBTBlockHolder(Location location, BlockPersistentData blockPersistentData){
        if(location == null)
            throw new NullPointerException("blockState can't be null!");
        this.blockLocation = location;
        if(blockPersistentData == null)
            throw new NullPointerException("blockPersistentData can't be null!");
        this.blockPersistentData = blockPersistentData;
        customBlockManager = VCorePaper.getInstance().getCustomBlockManager();
    }

    private <T> void saveMetaDataKey(String key, Class<T> type, T value){
        //blockState.setMetadata(key, new FixedMetadataValue(VCorePaper.getInstance(), value));
        blockPersistentData.getJsonObject().put(key,value);
        lastUse = System.currentTimeMillis();
    }

    private <T> T getMetaDataKey(String key, Class<T> type){
        lastUse = System.currentTimeMillis();

        Object dataFromJson = blockPersistentData.getJsonObject().get(key);

        //if(!blockState.hasMetadata(key)) {
        //    return VCoreUtil.getTypeUtil().castData(dataFromJson,type);
        //}
        //FixedMetadataValue fixedMetadataValue = (FixedMetadataValue) blockState.getMetadata(key).parallelStream().filter(metadataValue -> metadataValue.getOwningPlugin().equals(VCorePaper.getInstance())).findAny().orElse(null);
        //if(fixedMetadataValue == null)
            //return VCoreUtil.getTypeUtil().castData(dataFromJson,type);
        //Object dataFromBlockState = fixedMetadataValue.value();
        //if(dataFromBlockState == null)
            //return VCoreUtil.getTypeUtil().castData(dataFromJson,type);
        //if(dataFromJson == null)
            //return VCoreUtil.getTypeUtil().castData(dataFromBlockState,type);
        //if(!dataFromJson.equals(dataFromBlockState))
            //return VCoreUtil.getTypeUtil().castData(dataFromBlockState,type);
        return VCoreUtil.getTypeUtil().castData(dataFromJson,type);
    }

    @Override
    public Boolean getBoolean(String key) {
        try{
            getReadLock().lock();
            return getMetaDataKey(key, Boolean.class);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public Integer getInteger(String key) {
        try{
            getReadLock().lock();
            return getMetaDataKey(key, Integer.class);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public Double getDouble(String key) {
        try{
            getReadLock().lock();
            return getMetaDataKey(key, Double.class);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public String getString(String key) {
        try{
            getReadLock().lock();
            return getMetaDataKey(key, String.class);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public UUID getUUID(String key) {
        try{
            getReadLock().lock();
            String serialized = getMetaDataKey(key, String.class);
            if(serialized == null)
                throw new NullPointerException("No serialized UUID found for key "+key);
            return UUID.fromString(VCoreUtil.getTypeUtil().uuidFromBase64(serialized));
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public Long getLong(String key) {
        try{
            getReadLock().lock();
            return getMetaDataKey(key, Long.class);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public ItemStack getItemStack(String key) {
        try{
            getReadLock().lock();
            String serialized = getMetaDataKey(key, String.class);
            if(serialized == null)
                throw new NullPointerException("No serialized ItemStack found for key "+key);
            return Serializer.Base64ToItemStack(serialized);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public <T> T getObject(String key, Class<T> type) {
        try{
            getReadLock().lock();
            if(type.equals(ItemStack.class))
                return (T) getItemStack(key);
            else if(type.equals(Boolean.class))
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

            return getMetaDataKey(key, type);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public void setObject(String key, Object value) {
        try {
            getWriteLock().lock();
            if(value instanceof Boolean)
                saveMetaDataKey(key,Boolean.class,(Boolean) value);
            else if(value instanceof Integer)
                saveMetaDataKey(key,Integer.class,(Integer) value);
            else if(value instanceof Double)
                saveMetaDataKey(key,Double.class,(Double) value);
            else if(value instanceof String)
                saveMetaDataKey(key,String.class,(String) value);
            else if(value instanceof UUID)
                saveMetaDataKey(key, String.class, VCoreUtil.getTypeUtil().uuidToBase64(value.toString()));
            else if(value instanceof Long)
                saveMetaDataKey(key,Long.class,(Long) value);
            else if(value instanceof ItemStack) {
                String serialized = Serializer.itemStackToBase64((ItemStack) value);
                if(serialized != null)
                    saveMetaDataKey(key, String.class, serialized);
            }
            else
                saveMetaDataKey(key, Object.class,value);
            save();
        } finally {
            getWriteLock().unlock();
        }
    }

    @Override
    public Set<String> getKeys() {
        Set<String> keys = new HashSet<>();
        blockPersistentData.getJsonObject().keySet().forEach(o -> keys.add(o.toString()));
        return keys;
    }

    @Override
    public boolean hasKey(String key) {
        return blockPersistentData.getJsonObject().containsKey(key);
    }

    @Override
    public void removeKey(String key) {
        blockPersistentData.getJsonObject().remove(key);
        //blockState.removeMetadata(key,VCorePaper.getInstance());
    }

    @Override
    public void save() {
        lastUse = System.currentTimeMillis();
        blockPersistentData.getVBlockSaveFile().save();
    }

    public Lock getWriteLock() {
        return writeLock;
    }

    public Lock getReadLock() {
        return readLock;
    }
}
