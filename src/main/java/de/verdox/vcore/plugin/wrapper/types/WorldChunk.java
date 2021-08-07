/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.wrapper.types;

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

    public WorldChunk(String worldName, int x, int z){
        this.worldName = worldName;
        this.x = x;
        this.z = z;
        this.globalSpaceX = (16*x);
        this.globalSpaceZ = (16*z);
    }

    public WorldChunk setX(int x){
        return new WorldChunk(worldName,x, z);
    }

    public WorldChunk setZ(int z){
        return new WorldChunk(worldName,x, z);
    }

    public WorldChunk setWorldName(String worldName){
        return new WorldChunk(worldName,x, z);
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
}
