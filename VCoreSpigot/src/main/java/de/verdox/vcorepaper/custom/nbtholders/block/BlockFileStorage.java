/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbtholders.block;

import de.tr7zw.changeme.nbtapi.NBTFile;
import de.verdox.vcore.plugin.SystemLoadable;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.listener.VCoreListener;
import de.verdox.vcore.plugin.wrapper.types.WorldChunk;
import de.verdox.vcore.plugin.wrapper.types.WorldRegion;
import de.verdox.vcore.util.VCoreUtil;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldSaveEvent;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.08.2021 14:33
 */
public class BlockFileStorage extends VCoreListener.VCoreBukkitListener implements SystemLoadable {
    private final ExecutorService executor = Executors.newSingleThreadExecutor(new DefaultThreadFactory("NBTBlock IO Loader"));
    private final Map<String, WorldStorage> fileCache = new ConcurrentHashMap<>();

    public BlockFileStorage(VCorePlugin.Minecraft plugin) {
        super(plugin);
    }

    CompletableFuture<NBTFile> getNBTFile(WorldChunk worldChunk) {
        CompletableFuture<NBTFile> nbtFileFuture = new CompletableFuture<>();
        executor.submit(() -> {
            nbtFileFuture.complete(getNBTFileUnsafe(worldChunk));
        });
        return nbtFileFuture;
    }

    NBTFile getNBTFileUnsafe(WorldChunk worldChunk) {
        if (!fileCache.containsKey(worldChunk.worldName)) {
            return null;
        }
        return fileCache.get(worldChunk.worldName).getFileCache().get(worldChunk.getRegion());
    }

    NBTFile loadNBTFileUnsafe(File worldDirectory, WorldChunk worldChunk) {
        WorldRegion worldRegion = new WorldRegion(worldChunk);
        try {
            if (fileCache.containsKey(worldChunk.worldName) && fileCache.get(worldChunk.worldName).getFileCache().containsKey(worldRegion))
                return fileCache.get(worldChunk.worldName).getFileCache().get(worldRegion);
            NBTFile nbtFile = new NBTFile(new File(worldDirectory.getAbsolutePath() + "//VBlocks//" + worldRegion.toStringWithoutWorld() + ".nbt"));
            if (!fileCache.containsKey(worldChunk.worldName))
                fileCache.put(worldRegion.worldName, new WorldStorage(worldChunk.worldName));
            fileCache.get(worldChunk.worldName).getFileCache().put(worldRegion, nbtFile);
            return nbtFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    CompletableFuture<NBTFile> loadNBTFile(File worldDirectory, WorldChunk worldChunk) {
        CompletableFuture<NBTFile> nbtFileFuture = new CompletableFuture<>();
        executor.submit(() -> {
            nbtFileFuture.complete(loadNBTFileUnsafe(worldDirectory, worldChunk));
        });
        return nbtFileFuture;
    }

    CompletableFuture<NBTFile> loadNBTFile(WorldChunk worldChunk) {
        World world = Bukkit.getWorld(worldChunk.worldName);
        if (world == null)
            throw new IllegalStateException("Trying to load NBTFile of world that is not loaded");
        return loadNBTFile(world.getWorldFolder(), worldChunk);
    }

    NBTFile loadNBTFileUnsafe(WorldChunk worldChunk) {
        World world = Bukkit.getWorld(worldChunk.worldName);
        if (world == null)
            throw new IllegalStateException("Trying to load NBTFile of world that is not loaded");
        return loadNBTFileUnsafe(world.getWorldFolder(), worldChunk);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        Chunk chunk = e.getChunk();
        // Only load for already generated Chunks?
        WorldChunk worldChunk = new WorldChunk(e.getChunk().getWorld().getName(), chunk.getX(), chunk.getZ());
        WorldRegion worldRegion = new WorldRegion(worldChunk);

        File worldDirectory = e.getWorld().getWorldFolder();
        executor.submit(() -> {
            if (fileCache.containsKey(worldChunk.worldName))
                return;
            if (!fileCache.get(worldChunk.worldName).getFileCache().containsKey(worldRegion))
                return;
            plugin.consoleMessage("&8[&b" + e.getChunk().getWorld().getName() + "&8]&eLoading Region " + worldRegion, true);
            loadNBTFile(worldDirectory, worldChunk);
        });
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        Chunk chunk = e.getChunk();
        executor.submit(() -> {
            WorldChunk worldChunk = new WorldChunk(e.getChunk().getWorld().getName(), chunk.getX(), chunk.getZ());
            NBTFile nbtFile = getNBTFileUnsafe(worldChunk);
            if (nbtFile == null)
                return;

            if (!VCoreUtil.BukkitUtil.getBukkitWorldUtil().isRegionLoaded(worldChunk.getRegion()) && fileCache.containsKey(worldChunk.worldName) && fileCache.get(worldChunk.worldName).getFileCache().containsKey(worldChunk.getRegion())) {
                plugin.consoleMessage("&8[&b" + e.getChunk().getWorld().getName() + "&8]&eUnloading Region " + worldChunk.getRegion(), true);
                fileCache.get(worldChunk.worldName).getFileCache().remove(worldChunk.getRegion());
            }
            try {
                nbtFile.save();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    @EventHandler
    public void onSave(WorldSaveEvent e) {
        executor.submit(this::saveAll);
    }

    private void saveAll() {
        fileCache.values().parallelStream().forEach(worldStorage -> {
            worldStorage.getFileCache().forEach((worldRegion, nbtFile) -> {
                try {
                    nbtFile.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
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
