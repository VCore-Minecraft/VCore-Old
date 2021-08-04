package de.verdox.vcore.util;

import de.verdox.vcore.util.global.*;
import de.verdox.vcore.util.bukkit.GeometryUtil;
import de.verdox.vcore.util.bukkit.BukkitItemUtil;
import de.verdox.vcore.util.bukkit.BukkitPlayerUtil;
import de.verdox.vcore.util.bukkit.BukkitWorldUtil;
import de.verdox.vcore.util.bukkit.VanillaUtil;

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


    public static class BukkitUtil{
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
    }
    public static class BungeeCordUtil{

    }
}