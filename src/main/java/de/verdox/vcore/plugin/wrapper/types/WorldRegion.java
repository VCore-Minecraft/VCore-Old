/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.wrapper.types;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.08.2021 17:41
 */


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

    @Override
    public String toString() {
        return worldName + "_" + toStringWithoutWorld();
    }

    public String toStringWithoutWorld() {
        return "region_" + regionX + "_" + regionZ;
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
