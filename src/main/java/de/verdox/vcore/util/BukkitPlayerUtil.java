package de.verdox.vcore.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BukkitPlayerUtil {

    BukkitPlayerUtil(){}

    public void sendPlayerMessage(Player player, ChatMessageType chatMessageType, String message){
        player.spigot().sendMessage(chatMessageType,new TextComponent(ChatColor.translateAlternateColorCodes('&',message)));
    }

    public double getFacingRotation(Player player) {
        float yaw = player.getLocation().getYaw();
        if (yaw < 0) {
            yaw += 360;
        }
        // South
        if (yaw >= 315 || yaw < 45) {
            return 180d;
            // West
        } else if (yaw < 135) {
            return 270d;
            // North
        } else if (yaw < 225) {
            return 0d;
            // East
        } else if (yaw < 315) {
            return 90d;
        }
        return 0d;
    }

    public DirectionEnum getDirection(Location location){
        float yaw = location.getYaw();
        if (yaw < 0) {
            yaw += 360;
        }
        // South
        if (yaw >= 315 || yaw < 45) {
            return DirectionEnum.SOUTH;
            // West
        } else if (yaw < 135) {
            return DirectionEnum.WEST;
            // North
        } else if (yaw < 225) {
            return DirectionEnum.NORTH;
            // East
        } else if (yaw < 315) {
            return DirectionEnum.EAST;
        }
        return DirectionEnum.NORTH;
    }

    public double rotateOffsetX(Location location, double offsetX, double offsetZ){
        DirectionEnum direction = getDirection(location);
        switch (direction){
            case EAST: return offsetZ;
            case WEST: return -offsetZ;
            case SOUTH: return -offsetX;
            default: return offsetX;
        }
    }

}
