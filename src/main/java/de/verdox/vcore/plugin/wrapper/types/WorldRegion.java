/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.wrapper.types;


import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * World Region as a wrapper for Anvil Minecraft Chunk Format Regions
 */
public class WorldRegion {

    public final String worldName;
    public final int regionX;
    public final int regionZ;
    private final Set<WorldChunk> chunks = new HashSet<>();

    public WorldRegion(WorldChunk worldChunk) {
        this.worldName = worldChunk.worldName;
        this.regionX = worldChunk.x >> 5;
        this.regionZ = worldChunk.z >> 5;
    }

    public WorldRegion(String worldName, int regionX, int regionZ) {
        this.worldName = worldName;
        this.regionX = regionX;
        this.regionZ = regionZ;
    }

    public Set<WorldChunk> getChunks() {
        if (chunks.isEmpty()) {
            int minChunkX = regionX << 5;
            int minChunkZ = regionZ << 5;

            for (int chunkIndex = 0; chunkIndex < 32; chunkIndex++) {
                chunks.add(new WorldChunk(worldName, minChunkX + chunkIndex, minChunkZ + chunkIndex));
            }
        }
        return chunks;
    }

    public static long getRegionKey(int x, int z) {
        return (long) x & 0xffffffffL | ((long) z & 0xffffffffL) << 32;
    }

    public static String toString(int regionX, int regionZ) {
        return "region_" + regionX + "_" + regionZ;
    }

    @Override
    public String toString() {
        return worldName + "_" + toStringWithoutWorld();
    }

    public long getRegionKey() {
        return getRegionKey(regionX, regionZ);
    }

    public String toStringWithoutWorld() {
        return toString(regionX, regionZ);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorldRegion)) return false;
        WorldRegion that = (WorldRegion) o;
        return regionX == that.regionX && regionZ == that.regionZ && worldName.equals(that.worldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldName, regionX, regionZ);
    }
}
