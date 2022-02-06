/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.events.paper.blockevents;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.listener.VCoreListener;
import de.verdox.vcorepaper.impl.listener.VCorePaperListener;
import de.verdox.vcorepaper.impl.plugin.VCorePaperPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 17.09.2021 15:45
 */
public class BlockStateChangeListener extends VCorePaperListener {
    public BlockStateChangeListener(VCorePaperPlugin plugin) {
        super(plugin);
    }

    private boolean callBlockChangeState(Block block, @NotNull BlockData oldBlockData, @NotNull BlockData newBlockData, @NotNull BlockChangeStateEvent.Cause cause, @NotNull Event causingEvent) {
        BlockChangeStateEvent blockChangeStateEvent = new BlockChangeStateEvent(block, oldBlockData, newBlockData, cause);
        Bukkit.getPluginManager().callEvent(blockChangeStateEvent);
        return !blockChangeStateEvent.isCancelled();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void blockBreak(BlockBreakEvent e) {
        e.setCancelled(!callBlockChangeState(e.getBlock(), e.getBlock().getBlockData(), Bukkit.createBlockData(Material.AIR), BlockChangeStateEvent.Cause.PLAYER_BLOCK_BREAK, e));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void blockPlace(BlockPlaceEvent e) {
        e.setCancelled(!callBlockChangeState(e.getBlock(), e.getBlockReplacedState().getBlockData(), e.getBlockPlaced().getBlockData(), BlockChangeStateEvent.Cause.PLAYER_BLOCK_PLACE, e));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void pistonExtend(BlockPistonExtendEvent e) {

        Block pistonBlockingBlock = e.getBlocks().stream().filter(block -> block.getPistonMoveReaction().equals(PistonMoveReaction.BLOCK)).findAny().orElse(null);
        if (pistonBlockingBlock != null)
            return;

        for (Block block : e.getBlocks()) {
            PistonMoveReaction pistonMoveReaction = block.getPistonMoveReaction();
            boolean flag;

            if (pistonMoveReaction == PistonMoveReaction.BREAK) {
                flag = callBlockChangeState(block, block.getBlockData(), Bukkit.createBlockData(Material.AIR), BlockChangeStateEvent.Cause.PISTON_EXTEND, e);
            } else {
                Block blockBefore = block.getRelative(e.getDirection().getOppositeFace());
                Block blockAfter = block.getRelative(e.getDirection());
                // Piston that pushes
                if (blockBefore.getType().name().contains("PISTON") && !e.getBlocks().contains(blockBefore))
                    flag = callBlockChangeState(block, block.getBlockData(), Bukkit.createBlockData(Material.PISTON_HEAD), BlockChangeStateEvent.Cause.PISTON_EXTEND, e);
                else if (blockBefore.getPistonMoveReaction().equals(PistonMoveReaction.BREAK))
                    flag = callBlockChangeState(block, block.getBlockData(), Bukkit.createBlockData(Material.AIR), BlockChangeStateEvent.Cause.PISTON_EXTEND, e);
                else
                    flag = callBlockChangeState(block, block.getBlockData(), Bukkit.createBlockData(blockBefore.getType()), BlockChangeStateEvent.Cause.PISTON_EXTEND, e);

                if (!e.getBlocks().contains(blockAfter)) {
                    flag = callBlockChangeState(block, blockAfter.getBlockData(), block.getBlockData(), BlockChangeStateEvent.Cause.PISTON_EXTEND, e);
                }
            }
            if (!flag) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void pistonRetract(BlockPistonRetractEvent e) {
        for (Block block : e.getBlocks()) {
            PistonMoveReaction pistonMoveReaction = block.getPistonMoveReaction();

            BlockState airState = e.getBlock().getState(true);
            airState.setType(Material.AIR);

            boolean flag = true;

            if (pistonMoveReaction == PistonMoveReaction.BREAK)
                flag = callBlockChangeState(block, block.getBlockData(), Bukkit.createBlockData(Material.AIR), BlockChangeStateEvent.Cause.PISTON_RETRACT, e);
            else if (pistonMoveReaction == PistonMoveReaction.BLOCK || pistonMoveReaction == PistonMoveReaction.PUSH_ONLY)
                flag = callBlockChangeState(block, block.getBlockData(), block.getBlockData(), BlockChangeStateEvent.Cause.PISTON_RETRACT, e);
            else {
                Block blockAfter = block.getRelative(e.getDirection());
                if (e.getBlocks().contains(blockAfter)) {
                    flag = callBlockChangeState(block, block.getBlockData(), blockAfter.getBlockData(), BlockChangeStateEvent.Cause.PISTON_RETRACT, e);
                } else {
                    flag = callBlockChangeState(blockAfter, blockAfter.getBlockData(), block.getBlockData(), BlockChangeStateEvent.Cause.PISTON_RETRACT, e);
                    flag = callBlockChangeState(block, block.getBlockData(), Bukkit.createBlockData(Material.AIR), BlockChangeStateEvent.Cause.PISTON_RETRACT, e);
                }
            }
            if (!flag) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void blockFromTo(BlockFromToEvent e) {
        if (e.getBlock().getType().equals(Material.DRAGON_EGG)) {
            e.setCancelled(!callBlockChangeState(e.getBlock(), e.getBlock().getBlockData(), Bukkit.createBlockData(Material.AIR), BlockChangeStateEvent.Cause.DRAGON_EGG_TELEPORT, e));
            e.setCancelled(!callBlockChangeState(e.getBlock(), e.getToBlock().getBlockData(), Bukkit.createBlockData(Material.DRAGON_EGG), BlockChangeStateEvent.Cause.DRAGON_EGG_TELEPORT, e));
        } else
            e.setCancelled(!callBlockChangeState(e.getBlock(), e.getToBlock().getBlockData(), e.getBlock().getBlockData(), BlockChangeStateEvent.Cause.FLUID_FLOW, e));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void explodeEvent(BlockExplodeEvent e) {
        e.setCancelled(!callBlockChangeState(e.getBlock(), e.getBlock().getBlockData(), Bukkit.createBlockData(Material.AIR), BlockChangeStateEvent.Cause.BLOCK_EXPLODE, e));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void entityChangeBlock(EntityChangeBlockEvent e) {
        e.setCancelled(!callBlockChangeState(e.getBlock(), e.getBlock().getBlockData(), e.getBlockData(), BlockChangeStateEvent.Cause.ENTITY_CHANGE, e));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void blockDestroyEvent(BlockDestroyEvent e) {
        e.setCancelled(!callBlockChangeState(e.getBlock(), e.getBlock().getBlockData(), e.getNewState(), BlockChangeStateEvent.Cause.TRIGGERED_DESTRUCTION, e));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void emptyBucket(PlayerBucketEmptyEvent e) {
        Material fluidMaterial;
        if (e.getBucket().equals(Material.LAVA_BUCKET))
            fluidMaterial = Material.LAVA;
        else
            fluidMaterial = Material.WATER;
        e.setCancelled(!callBlockChangeState(e.getBlock(), Bukkit.createBlockData(Material.AIR), Bukkit.createBlockData(fluidMaterial), BlockChangeStateEvent.Cause.PLAYER_EMPTY_BUCKET, e));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void emptyBucket(PlayerBucketFillEvent e) {
        e.setCancelled(!callBlockChangeState(e.getBlock(), e.getBlock().getBlockData(), Bukkit.createBlockData(Material.AIR), BlockChangeStateEvent.Cause.PLAYER_FILL_BUCKET, e));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void blockGrow(BlockGrowEvent e) {
        e.setCancelled(!callBlockChangeState(e.getBlock(), e.getBlock().getBlockData(), e.getNewState().getBlockData(), BlockChangeStateEvent.Cause.PLAYER_EMPTY_BUCKET, e));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void blockFertilize(StructureGrowEvent e) {
        for (BlockState blockState : e.getBlocks()) {
            boolean flag;
            flag = callBlockChangeState(blockState.getBlock(), blockState.getBlock().getBlockData(), blockState.getBlockData(), BlockChangeStateEvent.Cause.GROW, e);
            if (!flag) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void blockFade(BlockFadeEvent e) {
        e.setCancelled(!callBlockChangeState(e.getBlock(), e.getBlock().getBlockData(), e.getNewState().getBlockData(), BlockChangeStateEvent.Cause.FADE, e));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onLeavesDecay(LeavesDecayEvent e) {
        e.setCancelled(!callBlockChangeState(e.getBlock(), e.getBlock().getBlockData(), Bukkit.createBlockData(Material.AIR), BlockChangeStateEvent.Cause.LEAVES_DECAY, e));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onSpongeAbsorb(SpongeAbsorbEvent e) {
        for (BlockState blockState : e.getBlocks()) {
            boolean flag;
            flag = callBlockChangeState(blockState.getBlock(), blockState.getBlock().getBlockData(), blockState.getBlockData(), BlockChangeStateEvent.Cause.SPONGE_ABSORB, e);
            if (!flag) {
                e.setCancelled(true);
                return;
            }
        }
    }
}
