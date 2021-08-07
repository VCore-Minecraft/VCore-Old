package de.verdox.vcore.util.bukkit.keys;

import de.verdox.vcore.plugin.wrapper.types.WorldChunk;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

public class SplitChunkKey extends ChunkKey{
    private final int yCoordinate;

    public SplitChunkKey(WorldChunk worldChunk, int yCoordinate) {
        super(worldChunk);
        this.yCoordinate = yCoordinate;
    }

    public ChunkKey getChunkKey(){
        return this;
    }

    public Location toLocation(World world){
        return new Location(world,this.worldChunk.globalSpaceX,yCoordinate,this.worldChunk.globalSpaceZ);
    }

    @Override
    public String toString() {
        return this.worldChunk.x+"_"+yCoordinate+"_"+this.worldChunk.z;
    }


}
