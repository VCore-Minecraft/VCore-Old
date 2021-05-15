package de.verdox.vcorepaper.custom.blocks;

import de.verdox.vcore.util.VCoreUtil;
import de.verdox.vcore.util.keys.ChunkKey;
import de.verdox.vcore.util.keys.LocationKey;
import de.verdox.vcore.util.keys.SplitChunkKey;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class VBlockFileStorage {

    private final VBlockManager vBlockManager;

    VBlockFileStorage(VBlockManager vBlockManager){
        this.vBlockManager = vBlockManager;
    }

    public Set<Location> findCustomBlockLocations(Chunk chunk){
        Set<Location> locations = new HashSet<>();
        new ChunkKey(chunk).splitChunkKey(chunk.getWorld())
                .parallelStream()
                .forEach(key -> {
                    Location location = key.toLocation(chunk.getWorld());
                    if(location == null)
                        return;
                    File folder = getOrCreateFolder(location);
                    try{
                        Files.walk(folder.toPath(),1).forEach(path -> {
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

    public File getOrCreateFolder(Location location){
        File worldFolder = location.getWorld().getWorldFolder();

        String chunkKey = new ChunkKey(location.getChunk()).toString();
        String splitChunkKey = new SplitChunkKey(location.getChunk(),location.getBlockY()).toString();


        File saveFolder = new File(worldFolder
                .getAbsolutePath()+"//VChunks//"+chunkKey+"//"+splitChunkKey);
        if(!saveFolder.exists() || !saveFolder.isDirectory())
            saveFolder.mkdirs();
        return saveFolder;
    }

    public JSONObject getBlockStateJsonObject(BlockState blockState){
        File jsonFile = getBlockStateJsonFile(blockState);
        if(jsonFile == null)
            return null;
        if(!jsonFile.exists()) {
            try {
                jsonFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return readFromFile(jsonFile);
    }

    private JSONObject readFromFile(File file){
        try {
            JSONParser jsonParser = new JSONParser();
            return (JSONObject) jsonParser.parse(new FileReader(file));
        } catch (IOException | ParseException e) { e.printStackTrace(); }
        return null;
    }

    public File getBlockStateJsonFile(BlockState blockState){
        File folder = getOrCreateFolder(blockState.getLocation());
        try {
            Path foundFile = Files.walk(folder.toPath(),1).parallel().filter(path -> {
                File file = path.toFile();
                return file.getName().contains(new LocationKey(blockState.getLocation()).toString());
            }).findAny().orElse(new File(folder.toPath()+"//"+new LocationKey(blockState.getLocation())+".json").toPath());
            return foundFile.toFile();
        } catch (IOException e) { e.printStackTrace(); }
        return null;
    }

}
