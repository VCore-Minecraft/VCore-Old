/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms.packetabstraction.wrapper.chunk;

import de.verdox.vcorepaper.nms.reflection.java.FieldReflection;
import net.minecraft.server.v1_16_R3.Packet;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_16_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 15.09.2021 21:40
 */
public class ChunkPacketWrapper_V_1_16_R3 extends ChunkPacketWrapper {

    public final FieldReflection.ReferenceField<Integer> chunkX = getReferenceClass().findField("a", Integer.class);
    public final FieldReflection.ReferenceField<Integer> chunkZ = getReferenceClass().findField("b", Integer.class);
    public final FieldReflection.ReferenceField<Integer> bitMaskLength = getReferenceClass().findField("c", Integer.class);
    public final FieldReflection.ReferenceField<int[]> biomes = getReferenceClass().findField("e", int[].class);
    public final FieldReflection.ReferenceField<byte[]> data = getReferenceClass().findField("f", byte[].class);

    public ChunkPacketWrapper_V_1_16_R3(Chunk chunk, int biomeSize, boolean modifyBlocks) {
        CraftChunk craftChunk = (CraftChunk) chunk;
        setPacket(getReferenceClass()
                .findConstructor(net.minecraft.server.v1_16_R3.Chunk.class, int.class, boolean.class)
                .instantiate(craftChunk.getHandle(), biomeSize, modifyBlocks));
        chunkX.of(getPacket());
        chunkZ.of(getPacket());
        bitMaskLength.of(getPacket());
        biomes.of(getPacket());
        data.of(getPacket());
    }

    @Override
    public void sendPlayer(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        craftPlayer.getHandle().playerConnection.sendPacket((Packet<?>) getPacket());
    }
}
