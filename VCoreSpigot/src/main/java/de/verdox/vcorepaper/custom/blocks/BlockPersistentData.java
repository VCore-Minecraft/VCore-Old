package de.verdox.vcorepaper.custom.blocks;

import de.verdox.vcore.util.keys.LocationKey;
import org.bukkit.Location;
import org.json.simple.JSONObject;

public class BlockPersistentData {

    private static final int SECONDS_NEED_TO_CLEAN = 1800;

    private long lastUse = System.currentTimeMillis();
    private final LocationKey locationKey;
    private final JSONObject jsonObject;
    private final Location location;

    public BlockPersistentData(LocationKey locationKey, JSONObject jsonObject, Location location){
        this.locationKey = locationKey;
        this.jsonObject = jsonObject;
        this.location = location;
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
}
