package de.verdox.vcore.util.bukkit.keys;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Objects;

public class LocationKey extends VCoreKey{

    private final String worldName;
    private final int x;
    private final int y;
    private final int z;

    public LocationKey(Location location){
        this.worldName = location.getWorld().getName();
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }

    public LocationKey(String key){
        String[] split = key.split("_");
        this.worldName = split[0];
        this.x = Integer.parseInt(split[1]);
        this.y = Integer.parseInt(split[2]);
        this.z = Integer.parseInt(split[3]);
    }

    public Location getLocation(){
        return new Location(Bukkit.getWorld(worldName),x,y,z);
    }

    @Override
    public String toString() {
        return worldName+"_"+x+"_"+y+"_"+z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocationKey)) return false;
        LocationKey that = (LocationKey) o;
        return x == that.x && y == that.y && z == that.z && Objects.equals(worldName, that.worldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldName, x, y, z);
    }
}
