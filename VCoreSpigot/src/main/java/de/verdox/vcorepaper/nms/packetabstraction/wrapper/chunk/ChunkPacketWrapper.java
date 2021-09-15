/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms.packetabstraction.wrapper.chunk;

import de.verdox.vcorepaper.nms.packetabstraction.wrapper.PacketWrapper;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 22.06.2021 02:56
 */
public abstract class ChunkPacketWrapper extends PacketWrapper {
    public ChunkPacketWrapper() {
        super("PacketPlayOutMapChunk");
    }
}
