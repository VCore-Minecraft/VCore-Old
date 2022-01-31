/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nms.packetabstraction.wrapper;

import de.verdox.vcorepaper.nms.reflection.java.ClassReflection;
import de.verdox.vcorepaper.nms.reflection.nms.MinecraftClassFinder;
import org.bukkit.entity.Player;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 22.06.2021 01:25
 */
public abstract class PacketWrapper {
    private final ClassReflection.ReferenceClass referenceClass;
    private Object packet;

    public PacketWrapper(String packetName) {
        referenceClass = MinecraftClassFinder.findMinecraftClass(MinecraftClassFinder.MinecraftPackage.NMS, null, packetName);
        packet = referenceClass.findConstructor().instantiate();
    }

    public Object getPacket() {
        return packet;
    }

    protected void setPacket(Object object) {
        packet = object;
    }

    public ClassReflection.ReferenceClass getReferenceClass() {
        return referenceClass;
    }

    public abstract void sendPlayer(Player player);
}
