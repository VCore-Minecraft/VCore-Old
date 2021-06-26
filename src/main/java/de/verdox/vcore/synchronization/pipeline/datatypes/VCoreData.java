/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.datatypes;

import de.verdox.vcore.synchronization.pipeline.PipelineManager;
import de.verdox.vcore.synchronization.pipeline.interfaces.DataManipulator;
import de.verdox.vcore.synchronization.pipeline.interfaces.VCoreSerializable;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.pipeline.parts.DataSynchronizer;

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

    public VCoreData(VCorePlugin<?,?> plugin, UUID objectUUID){
        this.plugin = plugin;
        this.objectUUID = objectUUID;
        this.dataManipulator = this.plugin.getDataPipeline().getGlobalCache().constructDataManipulator(this);
    }

    public UUID getObjectUUID() {
        return objectUUID;
    }

    public void save(boolean saveToGlobalStorage, boolean async){
        updateLastUse();
        this.dataManipulator.pushUpdate(this, async, () -> {
            if(!saveToGlobalStorage)
                return;
            plugin.getDataPipeline().getSynchronizer().synchronize(DataSynchronizer.DataSourceType.LOCAL, DataSynchronizer.DataSourceType.GLOBAL_STORAGE, getClass(), getObjectUUID());
        });
    }

    public void cleanUp(){
        this.dataManipulator.cleanUp();
        onCleanUp();
    }

    public VCorePlugin<?, ?> getPlugin() {
        return plugin;
    }

    public abstract void onLoad();
    public abstract void onCleanUp();
    public void debugToConsole(){
        serialize().forEach((s, o) -> {
            getPlugin().consoleMessage("&e"+s+"&7: &b"+o.toString(),2,true);
        });
    }

    public final boolean isExpired(){
        return (System.currentTimeMillis() - lastUse) > TimeUnit.SECONDS.toMillis(PipelineManager.EXPIRY_TIME_SECONDS);
    }

    public void updateLastUse(){
        this.lastUse = System.currentTimeMillis();
    }
}
