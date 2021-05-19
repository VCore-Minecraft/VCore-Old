package de.verdox.vcorepaper.custom.nbtholders;

import de.verdox.vcore.util.VCoreUtil;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.blocks.BlockPersistentData;
import de.verdox.vcorepaper.custom.blocks.VBlockManager;
import org.bukkit.block.BlockState;
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

    private final BlockState blockState;
    private final VBlockManager vBlockManager;
    private final BlockPersistentData blockPersistentData;
    private long lastUse = System.currentTimeMillis();

    public NBTBlockHolder(BlockState blockState, BlockPersistentData blockPersistentData){
        if(blockState == null)
            throw new NullPointerException("blockState can't be null!");
        this.blockState = blockState;
        if(blockPersistentData == null)
            throw new NullPointerException("blockPersistentData can't be null!");
        this.blockPersistentData = blockPersistentData;
        vBlockManager = VCorePaper.getInstance().getVBlockManager();
    }

    private <T> void saveMetaDataKey(String key, Class<T> type, T value){
        blockState.setMetadata(key, new FixedMetadataValue(VCorePaper.getInstance(), value));

        blockPersistentData.getJsonObject().put(key,value);
        lastUse = System.currentTimeMillis();
    }

    private <T> T getMetaDataKey(String key, Class<T> type){
        lastUse = System.currentTimeMillis();

        Object dataFromJson = blockPersistentData.getJsonObject().get(key);


        if(!blockState.hasMetadata(key)) {
            return VCoreUtil.getTypeUtil().castData(dataFromJson,type);
        }
        FixedMetadataValue fixedMetadataValue = (FixedMetadataValue) blockState.getMetadata(key).parallelStream().filter(metadataValue -> metadataValue.getOwningPlugin().equals(VCorePaper.getInstance())).findAny().orElse(null);
        if(fixedMetadataValue == null)
            return VCoreUtil.getTypeUtil().castData(dataFromJson,type);
        Object dataFromBlockState = fixedMetadataValue.value();
        if(dataFromBlockState == null)
            return VCoreUtil.getTypeUtil().castData(dataFromJson,type);
        if(dataFromJson == null)
            return VCoreUtil.getTypeUtil().castData(dataFromBlockState,type);
        if(!dataFromJson.equals(dataFromBlockState))
            return VCoreUtil.getTypeUtil().castData(dataFromBlockState,type);
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
            return getMetaDataKey(key, UUID.class);
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
    public <T> T getObject(String key, Class<T> type) {
        try{
            getReadLock().lock();
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
            if(value instanceof Integer)
                saveMetaDataKey(key,Integer.class,(Integer) value);
            if(value instanceof Double)
                saveMetaDataKey(key,Double.class,(Double) value);
            if(value instanceof String)
                saveMetaDataKey(key,String.class,(String) value);
            if(value instanceof UUID)
                saveMetaDataKey(key,UUID.class,(UUID) value);
            if(value instanceof Long)
                saveMetaDataKey(key,Long.class,(Long) value);
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
        blockState.removeMetadata(key,VCorePaper.getInstance());
    }

    @Override
    public void save() {
        lastUse = System.currentTimeMillis();
        vBlockManager.saveBlockPersistentData(blockState);
    }

    public Lock getWriteLock() {
        return writeLock;
    }

    public Lock getReadLock() {
        return readLock;
    }
}
