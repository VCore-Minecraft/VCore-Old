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
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldSaveEvent;

import java.io.File;
import java.io.IOException;
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
public class BlockFileStorage extends VCoreListener.VCoreBukkitListener implements SystemLoadable {
    private final ExecutorService executor = Executors.newSingleThreadScheduledExecutor(new DefaultThreadFactory("NBTBlock IO Loader"));
    private final Map<WorldRegion, NBTFile> fileCache = new ConcurrentHashMap<>();

    public BlockFileStorage(VCorePlugin.Minecraft plugin) {
        super(plugin);
    }

    NBTFile getNBTFile(WorldChunk worldChunk){
        WorldRegion worldRegion = new WorldRegion(worldChunk);
        return fileCache.get(worldRegion);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e){
        Chunk chunk = e.getChunk();
        File worldDirectory = e.getChunk().getWorld().getWorldFolder();
        // Only load for already generated Chunks?
        WorldChunk worldChunk = new WorldChunk(e.getChunk().getWorld().getName(),chunk.getX(),chunk.getZ());
        WorldRegion worldRegion = new WorldRegion(worldChunk);
        executor.submit(() -> {
            if(fileCache.containsKey(worldRegion))
                return;
            plugin.consoleMessage("&8[&b"+e.getChunk().getWorld().getName()+"&8]&eLoading Region "+worldRegion,true);
            try {
                NBTFile nbtFile = new NBTFile(new File(worldDirectory.getAbsolutePath()+"//VBlocks//"+worldRegion.toStringWithoutWorld()+".nbt"));
                fileCache.put(worldRegion,nbtFile);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e){
        Chunk chunk = e.getChunk();
        executor.submit(() -> {
            WorldChunk worldChunk = new WorldChunk(e.getChunk().getWorld().getName(),chunk.getX(),chunk.getZ());
            NBTFile nbtFile = getNBTFile(worldChunk);

            if(!VCoreUtil.BukkitUtil.getBukkitWorldUtil().isRegionLoaded(worldChunk.getRegion())) {
                plugin.consoleMessage("&8[&b"+e.getChunk().getWorld().getName()+"&8]&eUnloading Region "+worldChunk.getRegion(),true);
                fileCache.remove(worldChunk.getRegion());
            }
            try {
                nbtFile.save();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    @EventHandler
    public void onSave(WorldSaveEvent e){
        executor.submit(this::saveAll);
    }

    private void saveAll(){
        for (NBTFile value : fileCache.values()) {
            try {
                value.save();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public void shutdown() {
        executor.shutdown();
        try {
            plugin.consoleMessage("&eSaving CustomBlockData&7...",true);
            executor.awaitTermination(10, TimeUnit.SECONDS);
            saveAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
