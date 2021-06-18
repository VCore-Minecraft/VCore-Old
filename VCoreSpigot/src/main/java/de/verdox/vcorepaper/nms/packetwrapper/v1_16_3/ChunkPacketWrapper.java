/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms.packetwrapper.v1_16_3;

import de.verdox.vcore.util.VCoreUtil;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.PacketPlayOutMapChunk;
import org.bukkit.block.Biome;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 18.06.2021 02:02
 */
public class ChunkPacketWrapper {
    private PacketPlayOutMapChunk packetPlayOutMapChunk;

    public ChunkPacketWrapper(PacketPlayOutMapChunk packetPlayOutMapChunk){
        this.packetPlayOutMapChunk = packetPlayOutMapChunk;
    }

    public int getChunkX(){
        try {
            Field a = packetPlayOutMapChunk.getClass().getDeclaredField("a"); a.setAccessible(true);
            return (int) a.get(packetPlayOutMapChunk);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return Integer.MAX_VALUE;
        }
    }

    public int getChunkZ(){
        try {
            Field b = packetPlayOutMapChunk.getClass().getDeclaredField("b"); b.setAccessible(true);
            return (int) b.get(packetPlayOutMapChunk);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return Integer.MAX_VALUE;
        }
    }

    public int getSomething(){
        try {
            Field c = packetPlayOutMapChunk.getClass().getDeclaredField("c"); c.setAccessible(true);
            return (int) c.get(packetPlayOutMapChunk);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return Integer.MAX_VALUE;
        }
    }

    public int[] getBiomes(){
        try {
            Field e = packetPlayOutMapChunk.getClass().getDeclaredField("e"); e.setAccessible(true);
            return (int[]) e.get(packetPlayOutMapChunk);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return new int[0];
        }
    }

    public void setBiome(Biome biome){
        try {
            int[] biomes = getBiomes();
            int biomeID = VCoreUtil.getVanillaUtil().getBiomeID_1_16(biome);
            Arrays.fill(biomes, biomeID);
            Field e = packetPlayOutMapChunk.getClass().getDeclaredField("e"); e.setAccessible(true);
            e.set(packetPlayOutMapChunk, biomes);
        } catch (NoSuchFieldException | IllegalAccessException noSuchFieldException) {
            noSuchFieldException.printStackTrace();
        }
    }
}
