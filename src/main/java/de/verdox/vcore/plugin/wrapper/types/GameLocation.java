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
public class GameLocation {
    public final String worldName;
    public final double x;
    public final double y;
    public final double z;

    public GameLocation(@Nonnull String worldName, double x, double y, double z) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public GameLocation withWorld(@Nonnull String worldName) {
        return new GameLocation(worldName, x, y, z);
    }

    public GameLocation withX(double x) {
        return new GameLocation(worldName, x, y, z);
    }

    public GameLocation withY(double y) {
        return new GameLocation(worldName, x, y, z);
    }

    public GameLocation withZ(double z) {
        return new GameLocation(worldName, x, y, z);
    }

    @Override
    public String toString() {
        return "GameLocation{" +
                "worldName='" + worldName + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
