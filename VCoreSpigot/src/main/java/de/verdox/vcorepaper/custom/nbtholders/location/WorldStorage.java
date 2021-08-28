/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbtholders.location;

import de.tr7zw.changeme.nbtapi.NBTFile;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.wrapper.types.WorldRegion;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.08.2021 23:56
 */
public class WorldStorage {
    private final VCorePlugin<?, ?> plugin;
    private final File worldDirectory;
    private final String worldName;
    private final Map<Long, NBTFile> fileCache = new ConcurrentHashMap<>();

    public WorldStorage(VCorePlugin<?, ?> plugin, File worldDirectory, String worldName) {
        this.plugin = plugin;
        this.worldDirectory = worldDirectory;
        this.worldName = worldName;
    }

    public String getWorldName() {
        return worldName;
    }

    public boolean isRegionCached(int regionX, int regionZ) {
        return fileCache.containsKey(WorldRegion.getRegionKey(regionX, regionZ));
    }

    synchronized NBTFile cacheRegion(int regionX, int regionZ) {
        long regionKey = WorldRegion.getRegionKey(regionX, regionZ);
        if (isRegionCached(regionX, regionZ))
            return fileCache.get(regionKey);
        plugin.consoleMessage("&8[&b" + worldName + "&8] &eLoading Region&7: " + WorldRegion.toString(regionX, regionZ), true);
        return loadNBTFileUnsafe(regionX, regionZ);
    }

    synchronized void unCacheRegion(int regionX, int regionZ) {
        if (!isRegionCached(regionX, regionZ))
            return;
        long regionKey = WorldRegion.getRegionKey(regionX, regionZ);
        try {
            plugin.consoleMessage("&8[&b" + worldName + "&8] &eUnloading Region&7: " + WorldRegion.toString(regionX, regionZ), true);
            fileCache.remove(regionKey).save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    NBTFile getNBTFile(int regionX, int regionZ) {
        if (!isRegionCached(regionX, regionZ))
            return cacheRegion(regionX, regionZ);
        long regionKey = WorldRegion.getRegionKey(regionX, regionZ);
        return fileCache.get(regionKey);
    }

    public void saveAll() {
        fileCache.entrySet().iterator().forEachRemaining(longNBTFileEntry -> {
            try {
                longNBTFileEntry.getValue().save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private NBTFile loadNBTFileUnsafe(int regionX, int regionZ) {
        long regionKey = WorldRegion.getRegionKey(regionX, regionZ);
        try {
            if (fileCache.containsKey(regionKey))
                return fileCache.get(regionKey);
            WorldRegion worldRegion = new WorldRegion(worldName, regionX, regionZ);
            NBTFile nbtFile = new NBTFile(new File(worldDirectory.getAbsolutePath() + "//VBlocks//" + worldRegion.toStringWithoutWorld() + ".nbt"));
            fileCache.put(worldRegion.getRegionKey(), nbtFile);
            return nbtFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
