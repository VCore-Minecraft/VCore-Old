package de.verdox.vcorepaper.custom.blocks.files;

import com.google.gson.JsonObject;
import de.verdox.vcore.util.keys.ChunkKey;
import de.verdox.vcore.util.keys.LocationKey;
import de.verdox.vcore.util.keys.SplitChunkKey;
import de.verdox.vcorepaper.custom.blocks.BlockPersistentData;
import de.verdox.vcorepaper.custom.blocks.VBlockFileStorage;
import org.bukkit.Location;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class VBlockSaveFile {

    private final VBlockFileStorage vBlockFileStorage;
    private final Location blockLocation;
    private final File folder;
    private final File saveFile;
    private final JSONObject jsonObject;
    private final BlockPersistentData blockPersistentData;

    public VBlockSaveFile(VBlockFileStorage vBlockFileStorage, Location blockLocation){
        this.vBlockFileStorage = vBlockFileStorage;
        this.blockLocation = blockLocation;
        this.folder = getFolder();
        this.saveFile = getSaveFile();
        this.jsonObject = getJsonObject();
        blockPersistentData = getBlockPersistentData();
    }

    public void save(){
        if(blockPersistentData.isEmpty())
            return;
        try (FileWriter fileWriter = new FileWriter(getSaveFile())){
            jsonObject.writeJSONString(fileWriter);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean delete(){
        return getSaveFile().delete();
    }

    public BlockPersistentData getBlockPersistentData(){
        if(blockPersistentData != null)
            return blockPersistentData;
        BlockPersistentData blockPersistentData = new BlockPersistentData(new LocationKey(blockLocation),getJsonObject(),blockLocation);
        return blockPersistentData;
    }

    public File getFolder(){
        if(folder != null)
            return folder;
        File worldFolder = blockLocation.getWorld().getWorldFolder();
        int yCoordinate = blockLocation.getBlockY() / 16;

        String chunkKey = new ChunkKey(blockLocation.getChunk()).toString();
        String splitChunkKey = new SplitChunkKey(blockLocation.getChunk(),yCoordinate).toString();
        return new File(worldFolder
                .getAbsolutePath()+"//VChunks//"+chunkKey+"//"+splitChunkKey);
    }

    public JSONObject getJsonObject(){
        if(jsonObject != null)
            return jsonObject;
        JSONParser jsonParser = new JSONParser();
        try {
            return (JSONObject) jsonParser.parse(new FileReader(getSaveFile()));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public File getSaveFile(){
        if(this.saveFile != null)
            return saveFile;
        if(folder.isDirectory()){
            try{
                Path foundPath = Files.walk(folder.toPath(),1).parallel().filter(path -> {
                    File file = path.toFile();
                    return file.getName().contains(new LocationKey(blockLocation).toString());
                }).findAny().orElse(new File(folder.getAbsolutePath()+"//"+new LocationKey(blockLocation)+".json").toPath());
                return foundPath.toFile();
            }
            catch (IOException e){ }
        }
        return new File(folder.getAbsolutePath()+"//"+new LocationKey(blockLocation)+".json");
    }

}
