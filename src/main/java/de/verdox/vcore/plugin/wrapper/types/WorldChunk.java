/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.wrapper.types;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 07.08.2021 01:30
 */
public class WorldChunk {
    public final String worldName;
    public final int x;
    public final int z;
    public final int globalSpaceX;
    public final int globalSpaceZ;

    public WorldChunk(@NotNull String worldName, int x, int z) {
        this.worldName = worldName;
        this.x = x;
        this.z = z;
        this.globalSpaceX = (16 * x);
        this.globalSpaceZ = (16 * z);
    }

    public WorldRegion getRegion() {
        return new WorldRegion(this);
    }

    @Override
    public String toString() {
        return "WorldChunk{" +
                "worldName='" + worldName + '\'' +
                ", x=" + x +
                ", z=" + z +
                ", globalSpaceX=" + globalSpaceX +
                ", globalSpaceZ=" + globalSpaceZ +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorldChunk)) return false;
        WorldChunk that = (WorldChunk) o;
        return x == that.x && z == that.z && globalSpaceX == that.globalSpaceX && globalSpaceZ == that.globalSpaceZ && Objects.equals(worldName, that.worldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldName, x, z, globalSpaceX, globalSpaceZ);
    }
}
