/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.wrapper;

import de.verdox.vcore.plugin.wrapper.bungeecord.BungeePlatform;
import de.verdox.vcore.plugin.wrapper.spigot.SpigotPlatform;
import de.verdox.vcore.plugin.wrapper.types.GameLocation;
import de.verdox.vcore.plugin.wrapper.types.enums.PlayerGameMode;
import de.verdox.vcore.plugin.wrapper.types.enums.PlayerMessageType;
import de.verdox.vcore.util.VCoreUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 01.08.2021 20:01
 */
public class BukkitPlatformWrapperImpl implements PlatformWrapper {

    @Override
    public boolean isPlayerOnline(@Nonnull UUID playerUUID) {
        return Bukkit.getPlayer(playerUUID) != null;
    }

    @Override
    public boolean isPrimaryThread() {
        return Bukkit.isPrimaryThread();
    }

    @Override
    public void shutdown() {
        Bukkit.getServer().shutdown();
    }

    @Override
    public InetSocketAddress getPlayerAddress(@Nonnull UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null)
            return null;
        return player.getAddress();
    }

    @Override
    public SpigotPlatform getSpigotPlatform() {
        return new SpigotPlatform() {

            private Location getLocation(@Nonnull GameLocation gameLocation) {
                return new Location(Bukkit.getWorld(gameLocation.worldName), gameLocation.x, gameLocation.y, gameLocation.z);
            }

            private GameLocation getGameLocation(@Nonnull Location location) {
                return new GameLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
            }

            private GameMode getGameMode(@Nonnull PlayerGameMode playerGameMode) {
                return GameMode.valueOf(playerGameMode.name());
            }

            @Override
            public void setPlayerHealth(@Nonnull UUID playerUUID, double health) {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player == null)
                    return;
                player.setHealth(health);
            }

            @Override
            public void setPlayerFood(@Nonnull UUID playerUUID, int food) {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player == null)
                    return;
                player.setFoodLevel(food);
            }

            @Override
            public void teleportPlayer(@Nonnull UUID playerUUID, @Nonnull GameLocation gameLocation) {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player == null)
                    return;
                player.teleportAsync(getLocation(gameLocation));
            }

            @Override
            public void setGameMode(@Nonnull UUID playerUUID, @Nonnull PlayerGameMode playerGameMode) {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player == null)
                    return;
                player.setGameMode(getGameMode(playerGameMode));
            }

            @Override
            public void sendMessage(@Nonnull UUID playerUUID, @Nonnull String message, @Nonnull PlayerMessageType playerMessageType) {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player == null)
                    return;
                VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player, playerMessageType, ChatColor.translateAlternateColorCodes('&', message));
            }

            @Override
            public void broadcastMessage(@Nonnull String message, @Nonnull PlayerMessageType playerMessageType) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(onlinePlayer, playerMessageType, ChatColor.translateAlternateColorCodes('&', message));
                }
            }

            @Override
            public void kickPlayer(@Nonnull UUID playerUUID, @Nonnull String kickMessage) {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player == null)
                    return;
                player.kickPlayer(ChatColor.translateAlternateColorCodes('&', kickMessage));
            }

            @Override
            public void killPlayer(@Nonnull UUID playerUUID) {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player == null)
                    return;
                player.setHealth(0);
            }

            @Override
            public void clearInventory(@Nonnull UUID playerUUID) {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player == null)
                    return;
                player.getInventory().clear();
            }

            @Override
            public GameLocation getLocation(@Nonnull UUID playerUUID) {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player == null)
                    return null;
                return getGameLocation(player.getLocation());
            }

            @Override
            public double getTPS() {
                return Bukkit.getServer().getTPS()[0];
            }
        };
    }

    @Override
    public BungeePlatform getBungeePlatform() {
        return null;
    }
}
