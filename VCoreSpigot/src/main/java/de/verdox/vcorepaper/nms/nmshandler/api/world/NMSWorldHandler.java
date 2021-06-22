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

    void resetView(Player player);

    void refreshChunks(Player player);

    /**
     *
     * @param player Player to send Chunk to
     * @param chunk Chunk to send
     */
    void sendChunk( @NonNull Player player,  @NonNull org.bukkit.Chunk chunk);

    /**
     *
     * @param player Player to send Chunk to
     * @param chunk Chunk to send
     * @param biome Fake Biome to send
     */
    void sendFakeBiome(@NonNull Player player,  @NonNull org.bukkit.Chunk chunk,  @NonNull org.bukkit.block.Biome biome);

    /**
     *
     * @param player Player to send Chunk to
     * @param environment Fake DimensionType to send
     */
    void sendFakeDimension(@NonNull Player player, @NonNull org.bukkit.World.Environment environment);

    void sendFakeWorldBorder(@NonNull Player player, @NonNull Location center, @NonNegative double size);
}
