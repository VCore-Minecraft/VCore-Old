/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.util.global;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 06.07.2021 18:34
 */
public class TimeUtil {
    public String convertSecondsHMS(long seconds) {
        if (seconds >= 3600)
            return String.format("%02dh %02dmin %02ds", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
        else if (seconds >= 60)
            return String.format("%02dmin %02ds", (seconds % 3600) / 60, seconds % 60);
        else
            return String.format("%2ds", seconds % 60);
    }

    public String convertSecondsHM(long seconds) {
        if (seconds >= 3600)
            return String.format("%2dh %2dmin", seconds / 3600, (seconds % 3600) / 60);
        else
            return String.format("%2dmin", (seconds % 3600) / 60);
    }
}
