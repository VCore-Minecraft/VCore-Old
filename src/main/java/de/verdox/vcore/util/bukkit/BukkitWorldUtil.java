/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.util.bukkit;

import de.verdox.vcore.plugin.wrapper.types.GameLocation;
import de.verdox.vcore.plugin.wrapper.types.WorldChunk;
import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;

public class BukkitWorldUtil {

    public BukkitWorldUtil(){}

    public boolean blockLocationEqual(Location loc1, Location loc2){
        return loc1.getBlockX() == loc2.getBlockX()
                && loc1.getBlockY() == loc2.getBlockY()
                && loc1.getBlockZ() == loc2.getBlockZ();
    }

    //TODO: Funktioniert noch nicht
    public Block findStem(Block block){
        if(hasBlockStem(block, BlockFace.NORTH))
            return block.getRelative(BlockFace.NORTH);
        else if(hasBlockStem(block, BlockFace.SOUTH))
            return block.getRelative(BlockFace.SOUTH);
        else if(hasBlockStem(block, BlockFace.EAST))
            return block.getRelative(BlockFace.EAST);
        else if(hasBlockStem(block, BlockFace.WEST))
            return block.getRelative(BlockFace.WEST);
        return block;
    }

    public Material getWoolByColor(DyeColor dyeColor){
        return Material.valueOf(dyeColor.name()+"_WOOL");
    }

    public WorldChunk toWorldChunk(Chunk chunk){
        return new WorldChunk(chunk.getWorld().getName(),chunk.getX(),chunk.getZ());
    }

    public WorldChunk toWorldChunk(Location location){
        return new WorldChunk(location.getWorld().getName(),location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    public GameLocation toGameLocation(Chunk chunk){
        GameLocation gameLocation = new GameLocation();
        gameLocation.worldName = chunk.getWorld().getName();
        gameLocation.x = chunk.getX();
        gameLocation.y = 70;
        gameLocation.z = chunk.getZ();
        return gameLocation;
    }

    public GameLocation toGameLocation(Location location){
        GameLocation gameLocation = new GameLocation();
        gameLocation.worldName = location.getWorld().getName();
        gameLocation.x = location.getBlockX();
        gameLocation.y = location.getBlockY();
        gameLocation.z = location.getBlockZ();
        return gameLocation;
    }

    private boolean hasBlockStem(Block block, BlockFace stemBlockFaceLocation){
        if(block == null)
            return false;
        if(stemBlockFaceLocation == null)
            return false;
        Block stem = block.getRelative(stemBlockFaceLocation);
        if(!stem.getType().equals(Material.ATTACHED_MELON_STEM) && !stem.getType().equals(Material.ATTACHED_PUMPKIN_STEM))
            return false;
        Directional directional = (Directional) stem.getBlockData();
        BlockFace stemFacingTo = directional.getFacing();
        return stemFacingTo.getOppositeFace().equals(stemBlockFaceLocation);
    }
}
