/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms.nmshandler.api.world;

import de.verdox.vcorepaper.nms.NMSHandler;
import de.verdox.vcorepaper.nms.NMSVersion;
import de.verdox.vcorepaper.nms.nmshandler.v1_16_3.world.WorldHandler_V1_16_R3;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.checkerframework.checker.index.qual.NonNegative;
import reactor.util.annotation.NonNull;
import reactor.util.annotation.Nullable;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 16.06.2021 19:20
 */
public interface NMSWorldHandler extends NMSHandler {

    static NMSWorldHandler getRightHandler(NMSVersion nmsVersion){
        if(nmsVersion.equals(NMSVersion.V1_16_5)){
            return new WorldHandler_V1_16_R3();
        }
        throw new NotImplementedException("This Handler ["+NMSWorldHandler.class.getName()+"] is not implemented for NMS version: "+nmsVersion.getNmsVersionTag());
    }

    void resetView(Player player, Runnable callback);
    default void resetView(Player player){
        resetView(player, null);
    }

    void refreshChunks(Player player, Runnable callback);
    default void refreshChunks(Player player){
        refreshChunks(player, null);
    }


    /**
     *
     * @param player Player to send Chunk to
     * @param chunk Chunk to send
     */
    void refreshChunk(@NonNull Player player, @NonNull org.bukkit.Chunk chunk, @Nullable Runnable callback);
    default void refreshChunk(@NonNull Player player, @NonNull org.bukkit.Chunk chunk){
        refreshChunk(player, chunk, null);
    }

    /**
     *
     * @param player Player to send Chunk to
     * @param chunk Chunk to send
     * @param biome Fake Biome to send
     */
    void sendFakeBiome(@NonNull Player player,  @NonNull org.bukkit.Chunk chunk,  @NonNull org.bukkit.block.Biome biome, @Nullable Runnable callback);
    default void sendFakeBiome(@NonNull Player player,  @NonNull org.bukkit.Chunk chunk,  @NonNull org.bukkit.block.Biome biome){
        sendFakeBiome(player, chunk, biome, null);
    }

    /**
     *
     * @param player Player to send Chunk to
     * @param environment Fake DimensionType to send
     */
    void sendFakeDimension(@NonNull Player player, @NonNull org.bukkit.World.Environment environment, @Nullable Runnable callback);
    default void sendFakeDimension(@NonNull Player player, @NonNull org.bukkit.World.Environment environment){
        sendFakeDimension(player, environment, null);
    }

    default void refreshDimension(@NonNull Player player, @Nullable Runnable callback){
        sendFakeDimension(player, player.getWorld().getEnvironment(),callback);
    }
    default void refreshDimension(@NonNull Player player){
        refreshDimension(player, null);
    }

    void sendFakeWorldBorder(@NonNull Player player, @NonNull Location center, @NonNegative double size, @Nullable Runnable callback);
    default void sendFakeWorldBorder(@NonNull Player player, @NonNull Location center, @NonNegative double size){
        sendFakeWorldBorder(player, center, size, null);
    }

    void refreshWorldBorder(@NonNull Player player, @Nullable Runnable callback);
    default void refreshWorldBorder(@NonNull Player player){
        refreshWorldBorder(player, null);
    }
}
