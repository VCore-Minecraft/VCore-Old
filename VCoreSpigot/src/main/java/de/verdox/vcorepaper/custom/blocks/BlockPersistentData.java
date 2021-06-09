package de.verdox.vcorepaper.custom.blocks;

import de.verdox.vcore.util.keys.LocationKey;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.blocks.events.LoadVBlockDataEvent;
import de.verdox.vcorepaper.custom.blocks.events.UnloadVBlockDataEvent;
import de.verdox.vcorepaper.custom.blocks.files.VBlockSaveFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.json.simple.JSONObject;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class BlockPersistentData {

    private static final int SECONDS_NEED_TO_CLEAN = 1800;

    private long lastUse = System.currentTimeMillis();
    private final LocationKey locationKey;
    private final VBlockSaveFile vBlockSaveFile;
    private final Location location;
    private boolean save = true;
    private Set<Consumer<VBlock>> tickCallbacks = new HashSet<>();

    public BlockPersistentData(Location blockLocation, VBlockSaveFile vBlockSaveFile){
        this.locationKey = new LocationKey(blockLocation);
        this.vBlockSaveFile = vBlockSaveFile;
        this.location = blockLocation;
        //if(!jsonObject.containsKey("vBlock_blockData"))
        //    jsonObject.put("vBlock_blockData",location.getBlock().getBlockData().getAsString());
    }

    public Set<Consumer<VBlock>> getTickCallbacks() {
        return tickCallbacks;
    }

    public void addTickCallback(Consumer<VBlock> callback){
        tickCallbacks.add(callback);
    }

    public void clearTickCallbacks(){
        tickCallbacks.clear();
    }

    public boolean isEmpty(){
        if(getJsonObject().size() == 1 && getJsonObject().containsKey("vBlockBlockData"))
            return true;
        return getJsonObject().size() == 0;
    }

    void onDataLoad(){
        String blockDataAsString = getBlockDataAsString();
        if(blockDataAsString == null) {
            saveBlockData();
        }
        else {
            if(!validate())
                Bukkit.getScheduler().runTask(VCorePaper.getInstance(),() -> {
                    BlockData blockData = Bukkit.createBlockData(blockDataAsString);
                    location.getBlock().setBlockData(blockData);
                });
        }
        Bukkit.getPluginManager().callEvent(new LoadVBlockDataEvent(toVBlock()));
    }

    public VBlock toVBlock(){
        return new VBlock(location, VCorePaper.getInstance().getCustomBlockManager(),this);
    }

    private String getBlockDataAsString(){
        String blockDataString = (String) getJsonObject().get("vBlockBlockData");
        if(blockDataString == null)
            return null;
        if(!location.getBlock().getBlockData().getAsString().equals(blockDataString))
            return null;
        return blockDataString;
    }

    boolean validate(){
        if(!getJsonObject().containsKey("vBlockBlockData"))
            return true;
        String blockDataAsString = getBlockDataAsString();
        if(blockDataAsString == null)
            return true;
        return location.getBlock().getBlockData().getAsString().equals(blockDataAsString);
    }

    private void saveBlockData(){
        BlockData blockData = location.getBlock().getBlockData();
        getJsonObject().put("vBlockBlockData",blockData.getAsString());
    }

    void onDataUnload(){
        saveBlockData();
        Bukkit.getPluginManager().callEvent(new UnloadVBlockDataEvent(toVBlock()));
    }

    public void setSave(boolean save) {
        this.save = save;
    }

    public boolean isSave() {
        return save;
    }

    public LocationKey getLocationKey() {
        return locationKey;
    }

    public JSONObject getJsonObject() {
        lastUse = System.currentTimeMillis();
        return vBlockSaveFile.getJsonObject();
    }

    public Location getLocation() {
        return location;
    }

    public boolean readyToBeCleaned(){
        return System.currentTimeMillis() - lastUse > 1000L * SECONDS_NEED_TO_CLEAN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockPersistentData)) return false;
        BlockPersistentData that = (BlockPersistentData) o;
        return Objects.equals(getLocationKey(), that.getLocationKey()) && Objects.equals(getJsonObject(), that.getJsonObject());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocationKey(), getJsonObject());
    }

    public VBlockSaveFile getVBlockSaveFile() {
        return vBlockSaveFile;
    }
}
