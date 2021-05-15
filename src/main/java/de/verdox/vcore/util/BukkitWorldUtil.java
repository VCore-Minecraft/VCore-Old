package de.verdox.vcore.util;

import org.bukkit.Location;

public class BukkitWorldUtil {

    BukkitWorldUtil(){}

    public boolean blockLocationEqual(Location loc1, Location loc2){
        return loc1.getBlockX() == loc2.getBlockX()
                && loc1.getBlockY() == loc2.getBlockY()
                && loc1.getBlockZ() == loc2.getBlockZ();
    }
}
