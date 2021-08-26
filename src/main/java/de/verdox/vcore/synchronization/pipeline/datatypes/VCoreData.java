/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.datatypes;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.pipeline.annotations.VCoreDataProperties;
import de.verdox.vcore.synchronization.pipeline.interfaces.DataManipulator;
import de.verdox.vcore.synchronization.pipeline.interfaces.VCoreSerializable;
import de.verdox.vcore.synchronization.pipeline.parts.DataSynchronizer;
import de.verdox.vcore.util.global.AnnotationResolver;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 01:09
 */
public abstract class VCoreData implements VCoreSerializable {

    private final VCorePlugin<?, ?> plugin;
    private final UUID objectUUID;
    private final DataManipulator dataManipulator;
    private final long cleanTime;
    private final TimeUnit cleanTimeUnit;
    private long lastUse = System.currentTimeMillis();
    private boolean markedForRemoval = false;

    public VCoreData(VCorePlugin<?, ?> plugin, UUID objectUUID) {
        this.plugin = plugin;
        this.objectUUID = objectUUID;
        if (this.plugin.getServices().getPipeline().getGlobalCache() != null)
            this.dataManipulator = this.plugin.getServices().getPipeline().getGlobalCache().constructDataManipulator(this);
        else
            this.dataManipulator = new DataManipulator() {
                @Override
                public void cleanUp() {

                }

                @Override
                public void pushUpdate(VCoreData vCoreData, Runnable callback) {

                }

                @Override
                public void pushRemoval(VCoreData vCoreData, Runnable callback) {

                }
            };
        VCoreDataProperties dataContext = AnnotationResolver.getDataProperties(getClass());
        this.cleanTime = dataContext.time();
        this.cleanTimeUnit = dataContext.timeUnit();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VCoreData)) return false;
        VCoreData vCoreData = (VCoreData) o;
        return Objects.equals(getObjectUUID(), vCoreData.getObjectUUID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getObjectUUID());
    }

    public UUID getObjectUUID() {
        return objectUUID;
    }

    public void save(boolean saveToGlobalStorage) {
        updateLastUse();
        if (this.dataManipulator == null)
            return;
        this.dataManipulator.pushUpdate(this, () -> {
            if (!saveToGlobalStorage)
                return;
            plugin.getServices().getPipeline().getSynchronizer().synchronize(DataSynchronizer.DataSourceType.LOCAL, DataSynchronizer.DataSourceType.GLOBAL_STORAGE, getClass(), getObjectUUID());
        });
    }


    public void cleanUp() {
        this.dataManipulator.cleanUp();
        onCleanUp();
        plugin.async(dataManipulator::cleanUp);
    }

    public VCorePlugin<?, ?> getPlugin() {
        return plugin;
    }

    /**
     * Executed after a DataManipulator synced the object
     *
     * @param dataBeforeSync The data the object had before syncing
     */
    public void onSync(Map<String, Object> dataBeforeSync) {
    }

    /**
     * Executed after instantiation of the Object
     * Executed before Object is put into LocalCache
     */
    public void onCreate() {
    }

    /**
     * Executed after the object was deleted from local cache. Only it was deleted successfully
     */
    public void onDelete() {
    }

    /**
     * Executed after Data was loaded from Pipeline. Not if it was found in LocalCache
     */
    public void onLoad() {

    }

    /**
     * Executed before Data is cleared from LocalCache
     */
    public void onCleanUp() {
    }

    public void markForRemoval() {
        this.markedForRemoval = true;
    }

    public void unMarkRemoval() {
        this.markedForRemoval = false;
    }

    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    public void debugToConsole() {
        serialize().forEach((s, o) -> {
            getPlugin().consoleMessage("&e" + s + "&7: &b" + o.toString(), 2, true);
        });
    }

    public final boolean isExpired() {
        return (System.currentTimeMillis() - lastUse) > cleanTimeUnit.toMillis(cleanTime);
    }

    public void updateLastUse() {
        this.lastUse = System.currentTimeMillis();
    }

    public long getLastUse() {
        return lastUse;
    }

    @Override
    public Map<String, Object> serialize() {
        unMarkRemoval();
        return VCoreSerializable.super.serialize();
    }

    @Override
    public Map<String, Object> deserialize(Map<String, Object> serializedData) {
        unMarkRemoval();
        return VCoreSerializable.super.deserialize(serializedData);
    }

    public DataManipulator getDataManipulator() {
        return dataManipulator;
    }
}
