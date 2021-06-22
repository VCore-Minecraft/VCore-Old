/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms.packetabstraction.wrapper;

import de.verdox.vcorepaper.nms.reflection.java.FieldReflection;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;


/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 22.06.2021 01:43
 */
public abstract class WorldBorderPacketWrapper extends PacketWrapper {

    public WorldBorderPacketWrapper() {
        super("PacketPlayOutWorldBorder");
    }

    public static class V_1_16_R3 extends WorldBorderPacketWrapper{

        public final FieldReflection.ReferenceField<net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder.EnumWorldBorderAction>
                worldBorderAction = getReferenceClass().findField("a", net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder.EnumWorldBorderAction.class).of(getPacket());

        public final FieldReflection.ReferenceField<Double> x = getReferenceClass().findField("c", Double.class).of(getPacket());
        public final FieldReflection.ReferenceField<Double> z = getReferenceClass().findField("d", Double.class).of(getPacket());

        public final FieldReflection.ReferenceField<Integer> worldSize = getReferenceClass().findField("c", Integer.class).of(getPacket()).setField(29999984);

        public final FieldReflection.ReferenceField<Double> size = getReferenceClass().findField("f", Double.class).of(getPacket());
        public final FieldReflection.ReferenceField<Double> newSize = getReferenceClass().findField("e", Double.class).of(getPacket());
        public final FieldReflection.ReferenceField<Long> speed = getReferenceClass().findField("g", Long.class).of(getPacket());

        public final FieldReflection.ReferenceField<Integer> warningBlocks = getReferenceClass().findField("i", Integer.class).of(getPacket());
        public final FieldReflection.ReferenceField<Integer> warningTime = getReferenceClass().findField("h", Integer.class).of(getPacket());

        @Override
        public void sendPlayer(Player player) {
            CraftPlayer craftPlayer = (CraftPlayer) player;
            craftPlayer.getHandle().playerConnection.sendPacket((Packet<?>) getPacket());
        }
    }
}
