/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.old_blocks;

import de.verdox.vcore.performance.concurrent.CatchingRunnable;
import de.verdox.vcore.performance.efficiency.Benchmark;
import de.verdox.vcore.plugin.wrapper.types.WorldChunk;
import de.verdox.vcore.util.VCoreUtil;
import de.verdox.vcore.util.bukkit.keys.ChunkKey;
import de.verdox.vcore.util.bukkit.keys.LocationKey;
import de.verdox.vcore.util.bukkit.keys.SplitChunkKey;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.CustomDataManager;
import de.verdox.vcorepaper.custom.block.data.VBlockCustomData;
import de.verdox.vcorepaper.custom.old_blocks.files.VBlockSaveFile;
import de.verdox.vcorepaper.custom.old_blocks.files.VBlockStorage;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

public class CustomBlockManager extends CustomDataManager<Location, VBlockCustomData<?>, VBlock> {

    private final VBlockStorage vBlockStorage;
    private final Map<SplitChunkKey, Map<LocationKey, VBlockSaveFile>> cache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new DefaultThreadFactory("CustomBlockManager"));

    public CustomBlockManager(VCorePaper vCorePaper) {
        super(vCorePaper);
        vBlockStorage = new VBlockStorage(this);
        executor.scheduleAtFixedRate(new CatchingRunnable(() -> {
            Benchmark benchmark = new Benchmark();
            //long timeStamp = System.currentTimeMillis();
            doTick();
            long benchmarkTime = benchmark.logTime();
            if (benchmarkTime >= TimeUnit.MILLISECONDS.toMillis(100))
                VCorePaper.getInstance().consoleMessage("&cVBlockManagerTick took&7: &b" + benchmark.getLastTimeStamp() + "ms&8[&6" + getCacheSize() + "&8]", true);
        }), 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public <U extends VBlock> U wrap(Class<? extends U> type, Location blockLocation) {
        if (Bukkit.isPrimaryThread())
            getVCorePaper().consoleMessage("&4Loading / Creating VBlock with main thread&7!", false);
        VBlockSaveFile vBlockSaveFile = loadSaveFile(blockLocation);
        if (vBlockSaveFile == null)
            throw new NullPointerException("BlockPersistentData could not be created!");
        try {
            return type.getDeclaredConstructor(Location.class, CustomBlockManager.class, BlockPersistentData.class).newInstance(blockLocation, this, vBlockSaveFile.getBlockPersistentData());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public <U extends VBlock> U convertTo(Class<? extends U> type, VBlock customData) {
        try {
            return type.getDeclaredConstructor(Location.class, CustomBlockManager.class, BlockPersistentData.class).newInstance(customData.getBlockPersistentData().getLocation(), this, customData.getBlockPersistentData());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Internal method for BlockTicking
     */
    private void doTick() {
        cache.forEach((splitChunkKey, chunkCache) -> {
            chunkCache.forEach((locationKey, vBlockSaveFile) -> {
                BlockPersistentData blockPersistentData = vBlockSaveFile.getBlockPersistentData();
                for (Consumer<VBlock> tickCallback : blockPersistentData.getTickCallbacks()) {
                    tickCallback.accept(blockPersistentData.toVBlock());
                }
            });
        });
    }

    /**
     * Gets Number of cached Chunks
     *
     * @return number of cached chunks
     */
    public int getCachedChunkCount() {
        return (int) new HashSet<>(cache.keySet()).stream().map((Function<SplitChunkKey, Object>) SplitChunkKey::getChunkKey).distinct().count();
    }

    /**
     * Asynchronous callback for a VBlock that gets loaded from cache or storage
     *
     * @param location
     * @param callback
     */
    public void VBlockCallback(Location location, Consumer<VBlock> callback) {
        getVCorePaper().async(() -> {
            VBlock vBlock = wrap(VBlock.class, location);
            if (vBlock == null)
                return;
            callback.accept(vBlock);
        });
    }

    /**
     * Returns the cached ChunkData
     *
     * @param chunk
     * @return Set of all cached VBlockSaveFiles of the Chunk
     */
    public Set<VBlockSaveFile> getDataOfChunk(Chunk chunk) {
        Set<VBlockSaveFile> set = new HashSet<>();
        new ChunkKey(new WorldChunk(chunk.getWorld().getName(), chunk.getX(), chunk.getZ())).splitChunkKey().parallelStream().forEach(splitChunkKey -> {
            if (!cache.containsKey(splitChunkKey))
                return;
            set.addAll(cache.get(splitChunkKey).values());
        });
        return set;
    }

    /**
     * Check if Chunk is cached inside VBlock System
     *
     * @param chunk
     * @return Returns if any block inside the chunk is cached
     */
    public boolean isCached(Chunk chunk) {
        return new ChunkKey(new WorldChunk(chunk.getWorld().getName(), chunk.getX(), chunk.getZ())).splitChunkKey().stream().anyMatch(cache::containsKey);
    }

    /**
     * Get Number of Cached Blocks
     *
     * @return Number of cached blocks inside Cache
     */
    private int getCacheSize() {
        AtomicInteger counter = new AtomicInteger(0);
        cache.forEach((splitChunkKey, chunkCache) -> {
            counter.addAndGet(chunkCache.size());
        });
        return counter.get();
    }

    /**
     * Load a VBlock from Cache only.
     *
     * @param location
     * @return The VBlock or null if not cached
     */
    public VBlock getVBlock(Location location) {
        VBlockSaveFile vBlockSaveFile = getSaveFile(location);
        if (vBlockSaveFile == null)
            return null;
        return new VBlock(location, this, vBlockSaveFile.getBlockPersistentData());
    }

    /**
     * Loads a VBlockSaveFile from internal Cache
     *
     * @param location
     * @return
     */
    public VBlockSaveFile getSaveFile(Location location) {
        SplitChunkKey splitChunkKey = new SplitChunkKey(VCoreUtil.BukkitUtil.getBukkitWorldUtil().toWorldChunk(location), location.getBlockY());
        if (!cache.containsKey(splitChunkKey))
            cache.put(splitChunkKey, new ConcurrentHashMap<>());
        ConcurrentHashMap<LocationKey, VBlockSaveFile> saveFileSet = (ConcurrentHashMap<LocationKey, VBlockSaveFile>) cache.get(splitChunkKey);
        return saveFileSet.get(new LocationKey(location));
    }

    /**
     * Loads a VBlockSaveFile from Cache or Storage
     * If loaded from Storage the VBlockSaveFile will be put into Cache
     *
     * @param location
     * @return
     */
    public VBlockSaveFile loadSaveFile(Location location) {
        SplitChunkKey splitChunkKey = new SplitChunkKey(VCoreUtil.BukkitUtil.getBukkitWorldUtil().toWorldChunk(location), location.getBlockY());
        if (!cache.containsKey(splitChunkKey))
            cache.put(splitChunkKey, new ConcurrentHashMap<>());
        VBlockSaveFile foundSaveFile = cache.get(splitChunkKey).get(new LocationKey(location));

        if (foundSaveFile != null)
            return foundSaveFile;

        VBlockSaveFile vBlockSaveFile = vBlockStorage.findSaveFile(location);
        if (!vBlockSaveFile.getBlockPersistentData().isEmpty())
            cache.get(splitChunkKey).put(new LocationKey(location), vBlockSaveFile);
        vBlockSaveFile.getBlockPersistentData().onDataLoad();
        return vBlockSaveFile;
    }

    /**
     * Unloads the VBlockSaveFile from internal cache without saving.
     *
     * @param vBlockSaveFile
     * @return
     */
    public VBlockSaveFile unloadSaveFile(VBlockSaveFile vBlockSaveFile) {
        if (!cache.containsKey(vBlockSaveFile.getSplitChunkKey()))
            return vBlockSaveFile;
        cache.get(vBlockSaveFile.getSplitChunkKey()).remove(vBlockSaveFile.getLocationKey());
        vBlockSaveFile.getBlockPersistentData().onDataUnload();
        return vBlockSaveFile;
    }

    @Override
    protected VBlockCustomData<?> instantiateCustomData(Class<? extends VBlockCustomData<?>> dataClass) {
        try {
            return dataClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public VBlockStorage getVBlockStorage() {
        return vBlockStorage;
    }
}
