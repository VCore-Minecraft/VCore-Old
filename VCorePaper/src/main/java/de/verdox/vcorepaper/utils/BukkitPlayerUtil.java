/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.utils;

import de.verdox.vcore.plugin.wrapper.types.WorldChunk;
import de.verdox.vcore.plugin.wrapper.types.enums.PlayerMessageType;
import de.verdox.vcore.util.global.DirectionEnum;
import de.verdox.vcorepaper.utils.keys.ChunkKey;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class BukkitPlayerUtil {

    public static void sendPlayerMessage(Player player, ChatMessageType chatMessageType, String message) {
        player.spigot().sendMessage(chatMessageType, new TextComponent(ChatColor.translateAlternateColorCodes('&', message)));
    }

    public static RayTraceResult rayTraceEntities(@NotNull Player player, double maxDistance) {
        return player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getLocation().getDirection(), maxDistance, entity -> !entity.getType().equals(EntityType.PLAYER));
    }

    public static void sendPlayerMessage(Player player, PlayerMessageType playerMessageType, String message) {
        player.spigot().sendMessage(ChatMessageType.valueOf(playerMessageType.name()), new TextComponent(ChatColor.translateAlternateColorCodes('&', message)));
    }

    public static String serializePotionEffect(PotionEffect potionEffect) {
        int amplifier = potionEffect.getAmplifier();
        int duration = potionEffect.getDuration();
        String type = potionEffect.getType().getName();
        return type + ";" + duration + ";" + amplifier;
    }

    public static PotionEffect deSerializePotionEffect(String serialized) {
        String[] split = serialized.split(";");
        if (split.length != 3)
            throw new IllegalArgumentException("Wrong format of: " + serialized);
        PotionEffectType potionEffectType = PotionEffectType.getByName(split[0]);
        if (potionEffectType == null)
            throw new IllegalArgumentException("Unknown potion Effect Type: " + split[0]);
        int duration = Integer.parseInt(split[1]);
        int amplifier = Integer.parseInt(split[2]);
        return new PotionEffect(potionEffectType, duration, amplifier);
    }

    public static Set<ChunkKey> getChunksInServerViewDistance(Player player) {
        Set<ChunkKey> set = new HashSet<>();
        Chunk chunk = player.getLocation().getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        int viewDistance = Bukkit.getServer().getViewDistance();
        int viewDistanceInBlocks = viewDistance * 16;

        for (int x = chunkX - viewDistanceInBlocks; x <= chunkX + viewDistanceInBlocks; x += 16) {
            for (int z = chunkZ - viewDistanceInBlocks; z <= chunkZ + viewDistanceInBlocks; z += 16) {
                set.add(new ChunkKey(new WorldChunk(player.getLocation().getWorld().getName(), x, z)));
            }
        }
        return set;
    }

    public static Collection<Chunk> getChunksAroundPlayer(Player player) {
        return getChunksAround(player.getLocation());
    }

    public static Collection<Chunk> getChunksAround(Location location) {
        int[] offset = {-1, 0, 1};

        World world = location.getWorld();
        int baseX = location.getChunk().getX();
        int baseZ = location.getChunk().getZ();

        Collection<Chunk> chunksAroundPlayer = new HashSet<>();
        for (int x : offset) {
            for (int z : offset) {
                Chunk chunk = world.getChunkAt(baseX + x, baseZ + z);
                chunksAroundPlayer.add(chunk);
            }
        }
        return chunksAroundPlayer;
    }

    public static double getFacingRotation(Player player) {
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

    public static DirectionEnum getDirection(Location location) {
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

    public static double rotateOffsetX(Location location, double offsetX, double offsetZ) {
        DirectionEnum direction = getDirection(location);
        switch (direction) {
            case EAST:
                return offsetZ;
            case WEST:
                return -offsetZ;
            case SOUTH:
                return -offsetX;
            default:
                return offsetX;
        }
    }

}
