package de.verdox.vcorepaper.custom.old_blocks.files;

import de.verdox.vcore.plugin.wrapper.types.WorldChunk;
import de.verdox.vcore.util.bukkit.keys.ChunkKey;
import de.verdox.vcore.util.bukkit.keys.LocationKey;
import de.verdox.vcore.util.bukkit.keys.SplitChunkKey;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.old_blocks.CustomBlockManager;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class VBlockStorage {

    private final CustomBlockManager customBlockManager;
    private final Map<World, File> worldFolders = new ConcurrentHashMap<>();

    public VBlockStorage(CustomBlockManager customBlockManager){
        this.customBlockManager = customBlockManager;
        Bukkit.getWorlds().forEach(world -> {
            worldFolders.put(world, world.getWorldFolder());
            VCorePaper.getInstance().consoleMessage("&eFound world&7: &b"+world.getName(),true);
        });
    }

    public Set<Location> findCustomBlockLocations(Chunk chunk){
        Set<Location> locations = new HashSet<>();
        new ChunkKey(new WorldChunk(chunk.getWorld().getName(),chunk.getX(),chunk.getZ())).splitChunkKey()
                .forEach(key -> {
                    Location location = key.toLocation(chunk.getWorld());
                    if(location == null)
                        return;
                    File folder = getFolder(location);
                    if(!folder.isDirectory())
                        return;
                    try{
                        Files.walk(folder.toPath(),1).skip(1).forEach(path -> {
                            String locationString = FilenameUtils.removeExtension(path.toFile().getName());
                            Location locationOfCustomBlock = new LocationKey(locationString).getLocation();
                            if(locationOfCustomBlock == null)
                                return;
                            locations.add(locationOfCustomBlock);
                        });
                    }
                    catch (IOException e){e.printStackTrace();}
                });
        return locations;
    }

    File getFolder(Location location){
        if(!worldFolders.containsKey(location.getWorld()))
            worldFolders.put(location.getWorld(),location.getWorld().getWorldFolder());
        File worldFolder = worldFolders.get(location.getWorld());
        int yCoordinate = location.getBlockY() / 16;

        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;
        WorldChunk worldChunk = new WorldChunk(location.getWorld().getName(),chunkX,chunkZ);

        String chunkKey = new ChunkKey(worldChunk).toString();
        String splitChunkKey = new SplitChunkKey(worldChunk,yCoordinate).toString();
        return new File(worldFolder
                .getAbsolutePath()+"//VChunks//"+chunkKey+"//"+splitChunkKey);
    }

    public VBlockSaveFile findSaveFile(Location location){
        return new VBlockSaveFile(this,location);
    }

}
