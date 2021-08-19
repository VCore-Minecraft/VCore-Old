/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.wrapper.types;

import javax.annotation.Nonnull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 03.08.2021 19:57
 */
public class ServerLocation extends GameLocation {
    public final String serverName;

    public ServerLocation(@Nonnull String serverName, @Nonnull String worldName, double x, double y, double z) {
        super(worldName, x, y, z);
        this.serverName = serverName;
    }

    public ServerLocation withServerName(@Nonnull String serverName) {
        return new ServerLocation(serverName, worldName, x, y, z);
    }

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
