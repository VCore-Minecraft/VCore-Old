/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.pipeline.datatypes;

import de.verdox.vcore.pipeline.interfaces.DataManipulator;
import de.verdox.vcore.pipeline.interfaces.VCoreSerializable;
import de.verdox.vcore.pipeline.parts.cache.redis.RedisCache;
import de.verdox.vcore.plugin.VCorePlugin;

import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 01:09
 */
public abstract class VCoreData implements VCoreSerializable {

    private final VCorePlugin<?, ?> plugin;
    private final UUID objectUUID;
    private final DataManipulator dataManipulator;

    public VCoreData(VCorePlugin<?,?> plugin, UUID objectUUID){
        this.plugin = plugin;
        this.objectUUID = objectUUID;
        this.dataManipulator = this.plugin.getDataPipeline().getGlobalCache().constructDataManipulator(this);
    }

    public UUID getObjectUUID() {
        return objectUUID;
    }

    public void save(boolean async){
        this.dataManipulator.pushUpdate(this, async);
    }

    public void onCleanup(){
        this.dataManipulator.cleanUp();
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
}
