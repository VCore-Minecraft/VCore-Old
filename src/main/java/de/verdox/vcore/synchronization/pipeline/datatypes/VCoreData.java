/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.datatypes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonElement;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.pipeline.annotations.VCoreDataProperties;
import de.verdox.vcore.synchronization.pipeline.parts.PipelineDataSynchronizer;
import de.verdox.vcore.synchronization.pipeline.parts.manipulator.DataSynchronizer;
import de.verdox.vcore.util.global.AnnotationResolver;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 01:09
 */
public abstract class VCoreData implements PipelineData {
    private final UUID objectUUID;

    private transient final VCorePlugin<?, ?> plugin;
    private transient final DataSynchronizer dataSynchronizer;
    private transient final long cleanTime;
    private transient final TimeUnit cleanTimeUnit;
    private transient long lastUse = System.currentTimeMillis();
    private transient boolean markedForRemoval = false;
    private transient final Gson gson;

    public VCoreData(@NotNull VCorePlugin<?, ?> plugin, @NotNull UUID objectUUID) {
        Objects.requireNonNull(plugin, "plugin can't be null!");
        Objects.requireNonNull(objectUUID, "objectUUID can't be null!");
        this.plugin = plugin;
        this.objectUUID = objectUUID;

        this.gson = new GsonBuilder().setPrettyPrinting().serializeNulls()
                //.setExclusionStrategies(new ExclusionStrategy() {
                //    @Override
                //    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                //        return fieldAttributes.getAnnotation(VCorePersistentData.class) == null;
                //    }

                //    @Override
                //    public boolean shouldSkipClass(Class<?> aClass) {
                //        return false;
                //    }
                .registerTypeAdapter(getClass(), (InstanceCreator<PipelineData>) type -> this)
                .create();

        if (this.plugin.getServices().getPipeline().getSynchronizingService() != null)
            this.dataSynchronizer = this.plugin.getServices().getPipeline().getSynchronizingService().getDataSynchronizer(this);
        else
            this.dataSynchronizer = new DataSynchronizer() {
                @Override
                public void cleanUp() {

                }

                @Override
                public void pushUpdate(VCoreData vCoreData, Runnable callback) {
                    if (callback != null)
                        callback.run();
                }

                @Override
                public void pushRemoval(VCoreData vCoreData, Runnable callback) {
                    if (callback != null)
                        callback.run();
                }
            };
        VCoreDataProperties dataContext = AnnotationResolver.getDataProperties(getClass());
        this.cleanTime = dataContext.time();
        this.cleanTimeUnit = dataContext.timeUnit();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VCoreData vCoreData)) return false;
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
        if (this.dataSynchronizer == null) {
            plugin.getServices().getPipeline().getPipelineDataSynchronizer().synchronize(PipelineDataSynchronizer.DataSourceType.LOCAL, PipelineDataSynchronizer.DataSourceType.GLOBAL_STORAGE, getClass(), getObjectUUID());
            return;
        }
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

    @Override
    public JsonElement serialize() {
        unMarkRemoval();
        return gson.toJsonTree(this);
    }

    public String serializeToString() {
        return gson.toJson(serialize());
    }

    @Override
    public String deserialize(JsonElement jsonObject) {
        unMarkRemoval();
        gson.fromJson(jsonObject, getClass());
        return gson.toJson(jsonObject);
    }

    public DataSynchronizer getDataManipulator() {
        return dataSynchronizer;
    }
}
