/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.datatypes;

import com.google.gson.*;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.pipeline.annotations.VCoreDataProperties;
import de.verdox.vcore.synchronization.pipeline.parts.PipelineDataSynchronizer;
import de.verdox.vcore.synchronization.pipeline.parts.manipulator.DataSynchronizer;
import de.verdox.vcore.synchronization.pipeline.interfaces.VCoreSerializable;
import de.verdox.vcore.util.global.AnnotationResolver;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
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
    private final DataSynchronizer dataSynchronizer;
    private final long cleanTime;
    private final TimeUnit cleanTimeUnit;
    private long lastUse = System.currentTimeMillis();
    private boolean markedForRemoval = false;
    private final Gson gson;

    public VCoreData(@NotNull VCorePlugin<?, ?> plugin, @NotNull UUID objectUUID) {
        Objects.requireNonNull(plugin, "plugin can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");
        this.plugin = plugin;
        this.objectUUID = objectUUID;

        this.gson = new GsonBuilder().setPrettyPrinting().serializeNulls().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        }).registerTypeAdapter(getClass(), (InstanceCreator<VCoreData>) type -> this)
                .create();

        if (this.plugin.getServices().getPipeline().getGlobalCache() != null)
            this.dataSynchronizer = this.plugin.getServices().getPipeline().getSynchronizingService().getDataSynchronizer(this);
        else
            this.dataSynchronizer = new DataSynchronizer() {
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
        if (this.dataSynchronizer == null)
            return;
        this.dataSynchronizer.pushUpdate(this, () -> {
            if (!saveToGlobalStorage)
                return;
            plugin.getServices().getPipeline().getPipelineDataSynchronizer().synchronize(PipelineDataSynchronizer.DataSourceType.LOCAL, PipelineDataSynchronizer.DataSourceType.GLOBAL_STORAGE, getClass(), getObjectUUID());
        });
    }


    public void cleanUp() {
        this.dataSynchronizer.cleanUp();
        onCleanUp();
        plugin.async(dataSynchronizer::cleanUp);
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
     * Executed before the object is deleted from local cache.
     */
    public void onDelete() {
    }

    /**
     * Executed directly after Data was loaded from Pipeline. Not if it was found in LocalCache
     */
    public void onLoad() {

    }

    /**
     * Executed before onLoad and before onCreate everytime the data is being loaded into local cache.
     * You can use this function to load dependent data from pipeline that is directly associated with this data
     */
    public void loadDependentData() {

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

    public final boolean isExpired() {
        return (System.currentTimeMillis() - lastUse) > cleanTimeUnit.toMillis(cleanTime);
    }

    public void updateLastUse() {
        this.lastUse = System.currentTimeMillis();
    }

    public long getLastUse() {
        return lastUse;
    }

    @Nonnull
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

    public DataSynchronizer getDataManipulator() {
        return dataSynchronizer;
    }
}
