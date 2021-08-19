/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.wrapper.test;

import de.verdox.vcore.plugin.wrapper.types.WorldChunk;
import de.verdox.vcore.plugin.wrapper.types.WorldRegion;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.08.2021 17:53
 */
public class WrapperTest {
    public static void main(String[] args) {

        System.out.println("Testing World Regions");
        WorldChunk chunk = new WorldChunk("testWorld", 1, 10);
        System.out.println("Test with Chunk: " + chunk);
        WorldRegion worldRegion = chunk.getRegion();
        System.out.println("Region of Chunk: " + worldRegion);

        System.out.println("ChunkList of Region");
        worldRegion.getChunks().forEach(worldChunk -> {
            System.out.println(">> \t" + worldChunk + " | " + worldChunk.getRegion());
        });
    }
}
