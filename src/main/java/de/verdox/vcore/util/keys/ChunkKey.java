package de.verdox.vcore.util.keys;

import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ChunkKey extends VCoreKey{

    protected int x;
    protected int z;
    protected int globalX;
    protected int globalZ;
    private Chunk chunk;

    public ChunkKey(Chunk chunk){
        x = chunk.getX();
        z = chunk.getZ();
        globalX = chunk.getBlock(0,0,0).getLocation().getBlockX();
        globalZ = chunk.getBlock(0,0,0).getLocation().getBlockZ();
        this.chunk = chunk;
    }

    public ChunkKey(int chunkX, int chunkZ){
        x = chunkX;
        z = chunkZ;
    }

    public ChunkKey(String key){
        String[] split = key.split("_");
        x = Integer.parseInt(split[0]);
        z = Integer.parseInt(split[1]);
    }

    public Set<SplitChunkKey> splitChunkKey(World world){
        Set<SplitChunkKey> split = new HashSet<>();
        for(int y = 0; y <= 256; y+=16){
            split.add(new SplitChunkKey(getChunkIn(world),y));
        }
        return split;
    }

    public final boolean isChunkLoadedIn(World world){
        return world.isChunkLoaded(this.x,this.z);
    }

    public final Chunk getChunkIn(World world){
        return world.getChunkAt(x,z);
    }

    protected Chunk getChunk() {
        return chunk;
    }

    @Override
    public String toString() {
        return x+"_"+z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChunkKey)) return false;
        ChunkKey chunkKey = (ChunkKey) o;
        return x == chunkKey.x && z == chunkKey.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }
}
