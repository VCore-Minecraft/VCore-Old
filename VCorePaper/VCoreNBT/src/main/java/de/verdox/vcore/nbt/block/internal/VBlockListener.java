/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nbt.block.internal;

import de.verdox.vcore.events.paper.blockevents.BlockChangeStateEvent;
import de.verdox.vcore.nbt.VCoreNBTModule;
import de.verdox.vcore.nbt.block.VBlock;
import de.verdox.vcore.nbt.block.flags.VBlockFlag;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.listener.VCoreListener;
import de.verdox.vcorepaper.impl.listener.VCorePaperListener;
import de.verdox.vcorepaper.impl.plugin.VCorePaperPlugin;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.08.2021 22:16
 */
public class VBlockListener extends VCorePaperListener {
    private final VCoreNBTModule vCoreNBTModule;

    public VBlockListener(VCoreNBTModule vCoreNBTModule, VCorePaperPlugin plugin) {
        super(plugin);
        this.vCoreNBTModule = vCoreNBTModule;
    }

    private boolean cancelBlockEventByFlag(BlockEvent blockEvent, VBlockFlag vBlockFlag) {
        return cancelBlockEventByFlag(blockEvent, blockEvent.getBlock(), vBlockFlag);
    }

    private boolean cancelBlockEventByFlag(BlockEvent blockEvent, Block block, VBlockFlag vBlockFlag) {
        if (!(blockEvent instanceof Cancellable))
            return false;
        if (block == null)
            return false;
        VBlock.BlockBased vBlock = vCoreNBTModule.getCustomBlockProvider().getBlockDataManager().getVBlock(block);
        if (vBlock == null)
            return false;
        plugin.consoleMessage("&8[&eVBlockListener&8] &bFlag&7: &a" + vBlockFlag, 2, true);
        plugin.consoleMessage(vBlock.getSetBlockFlags().toString(), 3, true);
        ((Cancellable) blockEvent).setCancelled(vBlock.isFlagSet(vBlockFlag));
        if (((Cancellable) blockEvent).isCancelled())
            plugin.consoleMessage("&8[&eVBlockListener&8] &cCancelled " + blockEvent.getClass().getSimpleName(), 2, true);
        return vBlock.isFlagSet(vBlockFlag);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void blockFromTo(BlockFromToEvent e) {
        //plugin.consoleMessage("&8[&eVBlockListener&8] &eChecking " + e.getClass().getSimpleName(), 1, true);
        cancelBlockEventByFlag(e, VBlockFlag.DENY_BLOCK_LIQUID_EVENT);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void blockGrowEvent(BlockGrowEvent e) {
        //plugin.consoleMessage("&8[&eVBlockListener&8] &eChecking " + e.getClass().getSimpleName(), 1, true);
        cancelBlockEventByFlag(e, VBlockFlag.DENY_BLOCK_GROW_EVENT);

        //Block stemBlock = VCoreUtil.getBukkitWorldUtil().findStem(block);
        //TODO: Für Crops fertig machen
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void leavesDecayEvent(LeavesDecayEvent e) {
        //plugin.consoleMessage("&8[&eVBlockListener&8] &eChecking " + e.getClass().getSimpleName(), 1, true);
        cancelBlockEventByFlag(e, VBlockFlag.DENY_BLOCK_LEAVES_DECAY_EVENT);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void explodeEvent(BlockExplodeEvent e) {
        //plugin.consoleMessage("&8[&eVBlockListener&8] &eChecking " + e.getClass().getSimpleName(), 1, true);
        cancelBlockEventByFlag(e, VBlockFlag.DENY_BLOCK_EXPLODE_EVENT);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void dropItems(BlockDropItemEvent e) {
        //plugin.consoleMessage("&8[&eVBlockListener&8] &eChecking " + e.getClass().getSimpleName(), 1, true);
        cancelBlockEventByFlag(e, VBlockFlag.DENY_BLOCK_DROP_ITEMS_EVENT);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void blockPistonExtendEvent(BlockPistonExtendEvent e) {
        //plugin.consoleMessage("&8[&eVBlockListener&8] &eChecking " + e.getClass().getSimpleName(), 1, true);
        for (Block block : e.getBlocks()) {
            if (cancelBlockEventByFlag(e, block, VBlockFlag.DENY_BLOCK_PISTON_EVENT))
                return;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void blockPistonRetractEvent(BlockPistonRetractEvent e) {
        //plugin.consoleMessage("&8[&eVBlockListener&8] &eChecking " + e.getClass().getSimpleName(), 1, true);
        for (Block block : e.getBlocks()) {
            if (cancelBlockEventByFlag(e, block, VBlockFlag.DENY_BLOCK_PISTON_EVENT))
                return;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void blockBurnEvent(BlockBurnEvent e) {
        //plugin.consoleMessage("&8[&eVBlockListener&8] &eChecking " + e.getClass().getSimpleName(), 1, true);
        cancelBlockEventByFlag(e, VBlockFlag.DENY_BLOCK_BURN_EVENT);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockStateChange(BlockChangeStateEvent e) {
        if (e.getOldBlockState().getMaterial().isAir() || e.getOldBlockState().getMaterial().equals(e.getNewBlockState().getMaterial()))
            return;
        VBlock.BlockBased blockBased = vCoreNBTModule.getCustomBlockProvider().getBlockDataManager().getVBlock(e.getBlock());
        boolean preserveDataOnBreak = blockBased.isVBlock() && blockBased.isFlagSet(VBlockFlag.PRESERVE_DATA_ON_BREAK);
        if (!preserveDataOnBreak)
            blockBased.toNBTHolder().delete();
        VBlock.LocationBased locationBased = blockBased.asLocationBased();
        if (locationBased.isVBlock() && !preserveDataOnBreak)
            locationBased.toNBTHolder().delete();
    }

}
