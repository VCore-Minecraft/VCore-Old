/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nms.api.world;

import de.verdox.vcore.nms.NMSHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.NotNull;
import reactor.util.annotation.Nullable;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 16.06.2021 19:20
 */
public interface NMSWorldHandler extends NMSHandler {

    void resetView(Player player, Runnable callback);

    default void resetView(Player player) {
        resetView(player, null);
    }

    void refreshChunks(Player player, Runnable callback);

    default void refreshChunks(Player player) {
        refreshChunks(player, null);
    }


    /**
     * @param player Player to send Chunk to
     * @param chunk  Chunk to send
     */
    void refreshChunk(@NotNull Player player, @NotNull org.bukkit.Chunk chunk, @Nullable Runnable callback);

    default void refreshChunk(@NotNull Player player, @NotNull org.bukkit.Chunk chunk) {
        refreshChunk(player, chunk, null);
    }

    /**
     * @param player Player to send Chunk to
     * @param chunk  Chunk to send
     * @param biome  Fake Biome to send
     */
    void sendFakeBiome(@NotNull Player player, @NotNull org.bukkit.Chunk chunk, @NotNull org.bukkit.block.Biome biome, @Nullable Runnable callback);

    default void sendFakeBiome(@NotNull Player player, @NotNull org.bukkit.Chunk chunk, @NotNull org.bukkit.block.Biome biome) {
        sendFakeBiome(player, chunk, biome, null);
    }

    /**
     * @param player      Player to send Chunk to
     * @param environment Fake DimensionType to send
     */
    void sendFakeDimension(@NotNull Player player, @NotNull org.bukkit.World.Environment environment, @Nullable Runnable callback);

    default void sendFakeDimension(@NotNull Player player, @NotNull org.bukkit.World.Environment environment) {
        sendFakeDimension(player, environment, null);
    }

    default void refreshDimension(@NotNull Player player, @Nullable Runnable callback) {
        sendFakeDimension(player, player.getWorld().getEnvironment(), callback);
    }

    default void refreshDimension(@NotNull Player player) {
        refreshDimension(player, null);
    }

    void sendFakeWorldBorder(@NotNull Player player, @NotNull Location center, @NonNegative double size, @Nullable Runnable callback);

    default void sendFakeWorldBorder(@NotNull Player player, @NotNull Location center, @NonNegative double size) {
        sendFakeWorldBorder(player, center, size, null);
    }

    void refreshWorldBorder(@NotNull Player player, @Nullable Runnable callback);

    default void refreshWorldBorder(@NotNull Player player) {
        refreshWorldBorder(player, null);
    }
}