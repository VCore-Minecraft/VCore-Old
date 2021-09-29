/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbt.holders.location;

import de.tr7zw.changeme.nbtapi.NBTFile;
import de.tr7zw.changeme.nbtapi.NbtApiException;
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
    private final Map<WorldRegion, NBTFile> fileCache = new ConcurrentHashMap<>();

    public WorldStorage(VCorePlugin<?, ?> plugin, File worldDirectory, String worldName) {
        this.plugin = plugin;
        this.worldDirectory = worldDirectory;
        this.worldName = worldName;
    }

    public boolean isRegionCached(int regionX, int regionZ) {
        return fileCache.containsKey(new WorldRegion(worldName, regionX, regionZ));
    }

    NBTFile cacheRegion(int regionX, int regionZ) {
        if (isRegionCached(regionX, regionZ))
            return fileCache.get(new WorldRegion(worldName, regionX, regionZ));
        return loadNBTFileUnsafe(regionX, regionZ);
    }

    synchronized void unCacheRegion(int regionX, int regionZ) {
        if (!isRegionCached(regionX, regionZ))
            return;
        try {
            fileCache.remove(new WorldRegion(worldName, regionX, regionZ)).save();
            plugin.consoleMessage("&8[&b" + worldName + "&8] &bRegion unloaded&7: " + WorldRegion.toString(regionX, regionZ), true);
        } catch (IOException | NbtApiException e) {
            plugin.consoleMessage("&8[&b" + worldName + "&8] &bCould not save Region&7: " + WorldRegion.toString(regionX, regionZ), true);
            e.printStackTrace();
        }
    }

    NBTFile getNBTFile(int regionX, int regionZ) {
        if (!isRegionCached(regionX, regionZ))
            return cacheRegion(regionX, regionZ);
        return fileCache.get(new WorldRegion(worldName, regionX, regionZ));
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

    private synchronized NBTFile loadNBTFileUnsafe(int regionX, int regionZ) {
        WorldRegion worldRegion = new WorldRegion(worldName, regionX, regionZ);
        File file = new File(worldDirectory.getAbsolutePath() + "//VBlocks//" + worldRegion.toStringWithoutWorld() + ".nbt");
        try {
            if (fileCache.containsKey(worldRegion))
                return fileCache.get(worldRegion);
            NBTFile nbtFile = new NBTFile(file);
            fileCache.put(worldRegion, nbtFile);
            plugin.consoleMessage("&8[&b" + worldName + "&8] &aRegion loaded&7: " + worldRegion.toStringWithoutWorld(), true);
            return nbtFile;
        } catch (IOException e) {
            plugin.consoleMessage("&8[&b" + worldName + "&8] &4Error while loading Region&7: " + worldRegion.toStringWithoutWorld(), true);
            e.printStackTrace();
            return null;
        } catch (NbtApiException e) {
            plugin.consoleMessage("&8[&b" + worldName + "&8] &4Error while reading Region NBT-File&7: " + worldRegion.toStringWithoutWorld(), true);
            e.printStackTrace();
            file.delete();
            return null;
        }
    }
}
