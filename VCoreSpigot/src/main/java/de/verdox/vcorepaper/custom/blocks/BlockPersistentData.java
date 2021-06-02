package de.verdox.vcorepaper.custom.blocks;

import de.verdox.vcore.util.keys.LocationKey;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.blocks.events.LoadVBlockDataEvent;
import de.verdox.vcorepaper.custom.blocks.events.UnloadVBlockDataEvent;
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
    private final JSONObject jsonObject;
    private final Location location;
    private boolean save = true;
    private Set<Consumer<VBlock>> tickCallbacks = new HashSet<>();

    public BlockPersistentData(LocationKey locationKey, JSONObject jsonObject, Location location){
        this.locationKey = locationKey;
        this.jsonObject = jsonObject;
        this.location = location;

        //if(!jsonObject.containsKey("vBlock_blockData"))
        //    jsonObject.put("vBlock_blockData",location.getBlock().getBlockData().getAsString());
    }

    public Set<Consumer<VBlock>> getTickCallbacks() {
        return tickCallbacks;
    }

    void addTickCallback(Consumer<VBlock> callback){
        tickCallbacks.add(callback);
    }

    public boolean isEmpty(){
        if(jsonObject.size() == 1 && jsonObject.containsKey("vBlockBlockData"))
            return true;
        return jsonObject.size() == 0;
    }

    void onDataLoad(){
        BlockData blockData = getBlockData();
        if(blockData == null) {
            saveBlockData();
        }
        else {
            if(!location.getBlock().getType().equals(blockData.getMaterial()))
                Bukkit.getScheduler().runTask(VCorePaper.getInstance(),() -> location.getBlock().setBlockData(blockData));
        }
        Bukkit.getPluginManager().callEvent(new LoadVBlockDataEvent(toVBlock()));
    }

    public VBlock toVBlock(){
        return new VBlock(location.getBlock().getState(), VCorePaper.getInstance().getVBlockManager(),this);
    }

    private BlockData getBlockData(){
        String blockDataString = (String) jsonObject.get("vBlockBlockData");
        if(blockDataString == null)
            return null;
        BlockData blockData = Bukkit.createBlockData(blockDataString);
        if(!location.getBlock().getBlockData().equals(blockData))
            return null;
        return blockData;
    }

    boolean validate(){
        if(!jsonObject.containsKey("vBlockBlockData"))
            return true;
        BlockData blockData = getBlockData();
        if(blockData == null)
            return true;
        return location.getBlock().getType().equals(blockData.getMaterial());
    }

    private void saveBlockData(){
        BlockData blockData = location.getBlock().getBlockData();
        jsonObject.put("vBlockBlockData",blockData.getAsString());
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
        return jsonObject;
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
}
