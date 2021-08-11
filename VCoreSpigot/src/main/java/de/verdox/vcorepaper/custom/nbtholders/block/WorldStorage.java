/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbtholders.block;

import de.tr7zw.changeme.nbtapi.NBTFile;
import de.verdox.vcore.plugin.wrapper.types.WorldRegion;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.08.2021 23:56
 */
public class WorldStorage {
    private final String worldName;
    Map<WorldRegion, NBTFile> fileCache = new ConcurrentHashMap<>();

    public WorldStorage(String worldName){
        this.worldName = worldName;
    }

    public String getWorldName() {
        return worldName;
    }

    public Map<WorldRegion, NBTFile> getFileCache() {
        return fileCache;
    }
}
