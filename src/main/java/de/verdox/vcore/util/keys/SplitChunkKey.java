package de.verdox.vcore.util.keys;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

public class SplitChunkKey extends ChunkKey{
    private final int yCoordinate;

    public SplitChunkKey(Chunk chunk, int yCoordinate) {
        super(chunk);
        this.yCoordinate = yCoordinate;
    }

    public SplitChunkKey(String key){
        super(key);
        String[] split = key.split("_");
        x = Integer.parseInt(split[0]);
        yCoordinate = Integer.parseInt(split[1]);
        z = Integer.parseInt(split[2]);
    }

    public Location toLocation(World world){
        return new Location(world,x,yCoordinate,z);
    }

    @Override
    public String toString() {
        return x+"_"+yCoordinate+"_"+z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SplitChunkKey)) return false;
        if (!super.equals(o)) return false;
        SplitChunkKey that = (SplitChunkKey) o;
        return yCoordinate == that.yCoordinate && x == that.x && z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x,yCoordinate,z);
    }
}
