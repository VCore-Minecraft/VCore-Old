/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms.v1_16_3.world.wrapper;

import de.verdox.vcore.util.VCoreUtil;
import de.verdox.vcorepaper.nms.reflection.java.FieldReflection;
import net.minecraft.server.v1_16_R3.PacketPlayOutMapChunk;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder;
import org.bukkit.block.Biome;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 18.06.2021 02:02
 */
public class ChunkPacketWrapper_V1_16_R3 {
    private final PacketPlayOutMapChunk packetPlayOutMapChunk;

    public ChunkPacketWrapper_V1_16_R3(PacketPlayOutMapChunk packetPlayOutMapChunk){
        this.packetPlayOutMapChunk = packetPlayOutMapChunk;
    }

    public int getChunkX(){
        return FieldReflection.getField(PacketPlayOutMapChunk.class,"a", Integer.class).of(packetPlayOutMapChunk).readField();
    }

    public int getChunkZ(){
        return FieldReflection.getField(PacketPlayOutMapChunk.class,"b", Integer.class).of(packetPlayOutMapChunk).readField();
    }

    public int getSomething(){
        return FieldReflection.getField(PacketPlayOutMapChunk.class,"c", Integer.class).of(packetPlayOutMapChunk).readField();
    }

    public int[] getBiomes(){
        return FieldReflection.getField(PacketPlayOutMapChunk.class,"e", int[].class).of(packetPlayOutMapChunk).readField();
    }

    public void setBiome(Biome biome){
        int[] biomes = getBiomes();
        int biomeID = VCoreUtil.getVanillaUtil().getBiomeID_1_16(biome);
        Arrays.fill(biomes, biomeID);
        FieldReflection.getField(PacketPlayOutMapChunk.class,"e", int[].class).of(packetPlayOutMapChunk).setField(biomes);
    }
}
