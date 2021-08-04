/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.datatypes;

import de.verdox.vcore.synchronization.pipeline.annotations.VCoreDataContext;
import de.verdox.vcore.synchronization.pipeline.interfaces.DataManipulator;
import de.verdox.vcore.synchronization.pipeline.interfaces.VCoreSerializable;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.pipeline.parts.DataSynchronizer;
import de.verdox.vcore.synchronization.pipeline.parts.cache.GlobalCache;

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
    private long lastUse = System.currentTimeMillis();
    private final long cleanTime;
    private final TimeUnit cleanTimeUnit;

    public VCoreData(VCorePlugin<?,?> plugin, UUID objectUUID){
        this.plugin = plugin;
        this.objectUUID = objectUUID;
        if(this.plugin.getServices().getPipeline().getGlobalCache() != null)
            this.dataManipulator = this.plugin.getServices().getPipeline().getGlobalCache().constructDataManipulator(this);
        else
            this.dataManipulator = new DataManipulator() {
                @Override
                public void cleanUp() {

                }

                @Override
                public void pushUpdate(VCoreData vCoreData, Runnable callback) {

                }
            };
        VCoreDataContext dataContext = GlobalCache.getDataContext(getClass());
        this.cleanTime = dataContext.time();
        this.cleanTimeUnit = dataContext.timeUnit();
    }

    //TODO: Potenziell Fehleranfällig
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

    public void save(boolean saveToGlobalStorage){
        updateLastUse();
        if(this.dataManipulator == null)
            return;
        this.dataManipulator.pushUpdate(this, () -> {
            if(!saveToGlobalStorage)
                return;
            plugin.getServices().getPipeline().getSynchronizer().synchronize(DataSynchronizer.DataSourceType.LOCAL, DataSynchronizer.DataSourceType.GLOBAL_STORAGE, getClass(), getObjectUUID());
        });
    }

    public abstract void onSync(Map<String, Object> dataBeforeSync);

    public void cleanUp(){
        this.dataManipulator.cleanUp();
        onCleanUp();
        plugin.async(dataManipulator::cleanUp);
    }

    public VCorePlugin<?, ?> getPlugin() {
        return plugin;
    }

    public abstract void onCreate();
    public abstract void onLoad();
    public abstract void onCleanUp();
    public void debugToConsole(){
        serialize().forEach((s, o) -> {
            getPlugin().consoleMessage("&e"+s+"&7: &b"+o.toString(),2,true);
        });
    }

    public final boolean isExpired(){
        return (System.currentTimeMillis() - lastUse) > cleanTimeUnit.toMillis(cleanTime);
    }

    public void updateLastUse(){
        this.lastUse = System.currentTimeMillis();
    }

    public long getLastUse() {
        return lastUse;
    }
}
