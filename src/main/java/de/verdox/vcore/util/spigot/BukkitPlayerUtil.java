/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.util.spigot;

import de.verdox.vcore.util.DirectionEnum;
import de.verdox.vcore.util.keys.ChunkKey;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class BukkitPlayerUtil {

    public BukkitPlayerUtil(){}

    public void sendPlayerMessage(Player player, ChatMessageType chatMessageType, String message){
        player.spigot().sendMessage(chatMessageType,new TextComponent(ChatColor.translateAlternateColorCodes('&',message)));
    }

    public Set<ChunkKey> getChunksInServerViewDistance(Player player){
        Set<ChunkKey> set = new HashSet<>();
        Chunk chunk = player.getLocation().getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        int viewDistance = Bukkit.getServer().getViewDistance();

        for(int x = chunkX-viewDistance; x <= chunkX+viewDistance; x++){
            for(int z = chunkZ-viewDistance; z <= chunkZ+viewDistance; z++){
                set.add(new ChunkKey(x,z));
            }
        }
        return set;
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
