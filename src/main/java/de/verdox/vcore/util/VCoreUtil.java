/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.util;

import de.verdox.vcore.util.bukkit.*;
import de.verdox.vcore.util.global.MathUtil;
import de.verdox.vcore.util.global.RandomUtil;
import de.verdox.vcore.util.global.TimeUtil;
import de.verdox.vcore.util.global.TypeUtil;

public class VCoreUtil {
    public static TypeUtil getTypeUtil() {
        return new TypeUtil();
    }

    public static RandomUtil getRandomUtil() {
        return new RandomUtil();
    }

    public static MathUtil getMathUtil() {
        return new MathUtil();
    }

    public static TimeUtil getTimeUtil() {
        return new TimeUtil();
    }

    public static class BukkitUtil {
        public static VanillaUtil getVanillaUtil() {
            return new VanillaUtil();
        }

        public static BukkitItemUtil getBukkitItemUtil() {
            return new BukkitItemUtil();
        }

        public static BukkitWorldUtil getBukkitWorldUtil() {
            return new BukkitWorldUtil();
        }

        public static BukkitPlayerUtil getBukkitPlayerUtil() {
            return new BukkitPlayerUtil();
        }

        public static GeometryUtil getGeometryUtil() {
            return new GeometryUtil();
        }

        public static BukkitServerUtil getBukkitServerUtil() {
            return new BukkitServerUtil();
        }

        public static BukkitInventoryUtil getBukkitInventoryUtil() {
            return new BukkitInventoryUtil();
        }
    }

    public static class BungeeCordUtil {

    }
}