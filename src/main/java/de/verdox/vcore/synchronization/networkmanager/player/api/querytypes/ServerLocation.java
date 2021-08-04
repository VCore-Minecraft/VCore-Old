/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player.api.querytypes;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 03.08.2021 19:57
 */
public class ServerLocation extends GameLocation{
    public String serverName;

    @Override
    public String toString() {
        return "ServerLocation{" +
                "worldName='" + worldName + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", serverName='" + serverName + '\'' +
                '}';
    }
}
