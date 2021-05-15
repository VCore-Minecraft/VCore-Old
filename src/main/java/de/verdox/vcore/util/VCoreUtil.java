package de.verdox.vcore.util;

public class VCoreUtil {

    private static final GeometryUtil geometryUtil = new GeometryUtil();
    private static final BukkitPlayerUtil bukkitPlayerUtil = new BukkitPlayerUtil();
    private static final BukkitWorldUtil bukkitWorldUtil = new BukkitWorldUtil();

    public static GeometryUtil getGeometryUtil() {
        return geometryUtil;
    }

    public static BukkitPlayerUtil getBukkitPlayerUtil() {
        return bukkitPlayerUtil;
    }

    public static BukkitWorldUtil getBukkitWorldUtil() {
        return bukkitWorldUtil;
    }
}
