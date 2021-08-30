/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbtholders.location;

import de.verdox.vcore.plugin.SystemLoadable;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.listener.VCoreListener;
import de.verdox.vcore.plugin.wrapper.types.WorldChunk;
import de.verdox.vcore.plugin.wrapper.types.WorldRegion;
import de.verdox.vcore.util.VCoreUtil;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldSaveEvent;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.08.2021 14:33
 */
public class LocationNBTFileStorage extends VCoreListener.VCoreBukkitListener implements SystemLoadable {
    private final ExecutorService executor = Executors.newSingleThreadExecutor(new DefaultThreadFactory("NBTBlock IO Loader"));
    private final Map<String, WorldStorage> fileCache = new ConcurrentHashMap<>();

    public LocationNBTFileStorage(VCorePlugin.Minecraft plugin) {
        super(plugin);
    }

    public WorldStorage getWorldStorage(String worldName) {
        return fileCache.get(worldName);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        Chunk chunk = e.getChunk();
        // Only load for already generated Chunks?

        String worldName = e.getChunk().getWorld().getName();

        File worldDirectory = e.getWorld().getWorldFolder();
        executor.submit(() -> {
            if (!fileCache.containsKey(worldName)) {
                plugin.consoleMessage("&8[&b" + e.getChunk().getWorld().getName() + "&8] &eWorld loaded", false);
                fileCache.put(worldName, new WorldStorage(plugin, worldDirectory, worldName));
            }
            WorldStorage worldStorage = fileCache.get(worldName);
            int regionX = WorldChunk.getRegionX(chunk.getX());
            int regionZ = WorldChunk.getRegionZ(chunk.getZ());

            if (worldStorage.isRegionCached(regionX, regionZ))
                return;
            worldStorage.cacheRegion(regionX, regionZ);
        });
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        Chunk chunk = e.getChunk();
        String worldName = e.getChunk().getWorld().getName();
        executor.submit(() -> {
            if (!fileCache.containsKey(worldName))
                return;
            WorldStorage worldStorage = fileCache.get(worldName);
            int regionX = WorldChunk.getRegionX(chunk.getX());
            int regionZ = WorldChunk.getRegionZ(chunk.getZ());
            WorldRegion worldRegion = new WorldRegion(worldName, regionX, regionZ);
            if (!VCoreUtil.BukkitUtil.getBukkitWorldUtil().isRegionLoaded(worldRegion) && worldStorage.isRegionCached(regionX, regionZ)) {
                plugin.consoleMessage("&8[&b" + e.getChunk().getWorld().getName() + "&8] &eUnloading Region " + WorldRegion.toString(regionX, regionZ) + " &8[&6Chunk&7: &e" + chunk.getX() + " &7| &e" + chunk.getZ() + "&8]", true);
                worldStorage.unCacheRegion(regionX, regionZ);
            }
        });
    }

    @EventHandler
    public void onSave(WorldSaveEvent e) {
        executor.submit(this::saveAll);
    }

    private void saveAll() {
        fileCache.values().parallelStream().forEach(WorldStorage::saveAll);
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public void shutdown() {
        executor.shutdown();
        try {
            plugin.consoleMessage("&eSaving CustomBlockData&7...", true);
            executor.awaitTermination(10, TimeUnit.SECONDS);
            saveAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
