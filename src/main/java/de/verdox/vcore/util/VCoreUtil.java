package de.verdox.vcore.util;

import de.verdox.vcore.util.spigot.BukkitItemUtil;
import de.verdox.vcore.util.spigot.BukkitPlayerUtil;
import de.verdox.vcore.util.spigot.BukkitWorldUtil;
import de.verdox.vcore.util.spigot.VanillaUtil;

public class VCoreUtil {

    private static final GeometryUtil geometryUtil = new GeometryUtil();
    private static final BukkitPlayerUtil bukkitPlayerUtil = new BukkitPlayerUtil();
    private static final BukkitWorldUtil bukkitWorldUtil = new BukkitWorldUtil();
    private static final TypeUtil typeUtil = new TypeUtil();
    private static final RandomUtil randomUtil = new RandomUtil();
    private static final MathUtil mathUtil = new MathUtil();
    private static final MySQLUtil mySQLUtil = new MySQLUtil();
    private static final BukkitItemUtil bukkitItemUtil = new BukkitItemUtil();
    private static final VanillaUtil vanillaUtil = new VanillaUtil();
    private static final TimeUtil timeUtil = new TimeUtil();

    public static GeometryUtil getGeometryUtil() {
        return geometryUtil;
    }
    public static BukkitPlayerUtil getBukkitPlayerUtil() {
        return bukkitPlayerUtil;
    }
    public static BukkitWorldUtil getBukkitWorldUtil() {
        return bukkitWorldUtil;
    }
    public static TypeUtil getTypeUtil() {
        return typeUtil;
    }
    public static RandomUtil getRandomUtil() {
        return randomUtil;
    }
    public static MathUtil getMathUtil() {
        return mathUtil;
    }
    public static MySQLUtil getMySQLUtil() {return mySQLUtil;}
    public static BukkitItemUtil getBukkitItemUtil() {
        return bukkitItemUtil;
    }
    public static VanillaUtil getVanillaUtil() {
        return vanillaUtil;
    }
    public static TimeUtil getTimeUtil() {
        return timeUtil;
    }
}
