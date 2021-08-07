package de.verdox.vcorepaper.custom.blocks.files;

import de.verdox.vcore.util.VCoreUtil;
import de.verdox.vcore.util.bukkit.keys.LocationKey;
import de.verdox.vcore.util.bukkit.keys.SplitChunkKey;
import de.verdox.vcorepaper.custom.blocks.BlockPersistentData;
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

    private VBlockStorage vBlockStorage;
    private final Location blockLocation;
    private final File folder;
    private final File saveFile;
    private final JSONObject jsonObject;
    private final BlockPersistentData blockPersistentData;

    public VBlockSaveFile(VBlockStorage vBlockStorage, Location blockLocation){
        this.vBlockStorage = vBlockStorage;
        this.blockLocation = blockLocation;
        this.folder = getFolder();
        this.saveFile = getSaveFile();
        this.jsonObject = getJsonObject();
        blockPersistentData = getBlockPersistentData();
    }

    public void save(){
        if(blockPersistentData.isEmpty()) {
            delete();
            return;
        }
        try{
            if(!getSaveFile().exists()) {
                folder.mkdirs();
                getSaveFile().createNewFile();
            }
            try (FileWriter fileWriter = new FileWriter(getSaveFile())){
                jsonObject.writeJSONString(fileWriter);
                fileWriter.flush();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public boolean delete(){
        return getSaveFile().delete();
    }

    public BlockPersistentData getBlockPersistentData(){
        if(blockPersistentData != null)
            return blockPersistentData;
        return new BlockPersistentData(blockLocation, this);
    }

    File getFolder(){
        if(folder != null)
            return folder;
        return vBlockStorage.getFolder(blockLocation);
    }

    public JSONObject getJsonObject(){
        if(jsonObject != null)
            return jsonObject;
        if(saveFile.exists()){
            JSONParser jsonParser = new JSONParser();
            try {
                return (JSONObject) jsonParser.parse(new FileReader(getSaveFile()));
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
        return new JSONObject();
    }

    File getSaveFile(){
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

    public Location getBlockLocation() {
        return blockLocation;
    }

    public SplitChunkKey getSplitChunkKey(){
        return new SplitChunkKey(VCoreUtil.BukkitUtil.getBukkitWorldUtil().toWorldChunk(blockLocation.getChunk()), blockLocation.getBlockY());
    }

    public LocationKey getLocationKey(){
        return new LocationKey(blockLocation);
    }
}
