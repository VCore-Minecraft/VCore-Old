/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms.packetabstraction;

import com.comphenix.protocol.events.PacketContainer;

import java.lang.reflect.Field;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 13.07.2021 23:24
 */
public class Util {
    public static void debugPacketContainer(PacketContainer packetContainer){
        System.out.println("");
        System.out.println(packetContainer.getHandle().getClass().getSimpleName());
        for (Field field : packetContainer.getHandle().getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                System.out.println(field.getName()+": "+field.get(packetContainer.getHandle()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
