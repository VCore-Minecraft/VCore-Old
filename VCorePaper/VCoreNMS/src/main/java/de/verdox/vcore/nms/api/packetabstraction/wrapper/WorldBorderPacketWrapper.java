/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nms.api.packetabstraction.wrapper;


/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 22.06.2021 01:43
 */
public abstract class WorldBorderPacketWrapper extends PacketWrapper {

    public WorldBorderPacketWrapper() {
        super("PacketPlayOutWorldBorder");
    }
}
