/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.block.internal;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.listener.VCoreListener;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.block.VBlock;
import de.verdox.vcorepaper.custom.block.flags.VBlockFlag;
import de.verdox.vcorepaper.custom.nbtholders.location.NBTLocation;
import de.verdox.vcorepaper.custom.nbtholders.location.event.nbtlocation.NBTBlockDeleteEvent;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.*;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.08.2021 22:16
 */
public class VBlockListener extends VCoreListener.VCoreBukkitListener {
    public VBlockListener(VCorePlugin.Minecraft plugin) {
        super(plugin);
    }

    private boolean cancelBlockEventByFlag(BlockEvent blockEvent, VBlockFlag vBlockFlag) {
        return cancelBlockEventByFlag(blockEvent, blockEvent.getBlock(), vBlockFlag);
    }

    private boolean cancelBlockEventByFlag(BlockEvent blockEvent, Block block, VBlockFlag vBlockFlag) {
        if (!(blockEvent instanceof Cancellable))
            return false;
        if (block == null)
            return false;
        VBlock.BlockBased vBlock = VCorePaper.getInstance().getCustomBlockManager().getBlockDataManager().getVBlock(block);
        if (vBlock == null)
            return false;
        plugin.consoleMessage("&8[&eVBlockListener&8] &bFlag&7: &a" + vBlockFlag, 2, true);
        plugin.consoleMessage(vBlock.getSetBlockFlags().toString(), 3, true);
        ((Cancellable) blockEvent).setCancelled(vBlock.isFlagSet(vBlockFlag));
        if (((Cancellable) blockEvent).isCancelled())
            plugin.consoleMessage("&8[&eVBlockListener&8] &cCancelled " + blockEvent.getClass().getSimpleName(), 2, true);
        return vBlock.isFlagSet(vBlockFlag);
    }

    @EventHandler
    public void onDelete(NBTBlockDeleteEvent e) {
        NBTLocation nbtLocation = e.getNbtBlock();
        if (!nbtLocation.getPersistentDataContainer().hasKey(VBlockFlag.PRESERVE_DATA_ON_BREAK.getNbtTag()))
            return;
        e.setCancelled(nbtLocation.getPersistentDataContainer().getBoolean(VBlockFlag.PRESERVE_DATA_ON_BREAK.getNbtTag()));
    }

    @EventHandler
    public void blockFromTo(BlockFromToEvent e) {
        plugin.consoleMessage("&8[&eVBlockListener&8] &eChecking " + e.getClass().getSimpleName(), 1, true);
        cancelBlockEventByFlag(e, VBlockFlag.DENY_BLOCK_LIQUID_EVENT);
    }

    @EventHandler
    public void blockGrowEvent(BlockGrowEvent e) {
        plugin.consoleMessage("&8[&eVBlockListener&8] &eChecking " + e.getClass().getSimpleName(), 1, true);
        cancelBlockEventByFlag(e, VBlockFlag.DENY_BLOCK_GROW_EVENT);

        //Block stemBlock = VCoreUtil.getBukkitWorldUtil().findStem(block);
        //TODO: Für Crops fertig machen
    }

    @EventHandler
    public void leavesDecayEvent(LeavesDecayEvent e) {
        plugin.consoleMessage("&8[&eVBlockListener&8] &eChecking " + e.getClass().getSimpleName(), 1, true);
        cancelBlockEventByFlag(e, VBlockFlag.DENY_BLOCK_LEAVES_DECAY_EVENT);
    }

    @EventHandler
    public void explodeEvent(BlockExplodeEvent e) {
        plugin.consoleMessage("&8[&eVBlockListener&8] &eChecking " + e.getClass().getSimpleName(), 1, true);
        cancelBlockEventByFlag(e, VBlockFlag.DENY_BLOCK_EXPLODE_EVENT);
    }

    @EventHandler
    public void dropItems(BlockDropItemEvent e) {
        plugin.consoleMessage("&8[&eVBlockListener&8] &eChecking " + e.getClass().getSimpleName(), 1, true);
        cancelBlockEventByFlag(e, VBlockFlag.DENY_BLOCK_DROP_ITEMS_EVENT);
    }

    @EventHandler
    public void blockPistonExtendEvent(BlockPistonExtendEvent e) {
        plugin.consoleMessage("&8[&eVBlockListener&8] &eChecking " + e.getClass().getSimpleName(), 1, true);
        for (Block block : e.getBlocks()) {
            if (cancelBlockEventByFlag(e, block, VBlockFlag.DENY_BLOCK_PISTON_EVENT))
                return;
        }
    }

    @EventHandler
    public void blockPistonRetractEvent(BlockPistonRetractEvent e) {
        plugin.consoleMessage("&8[&eVBlockListener&8] &eChecking " + e.getClass().getSimpleName(), 1, true);
        for (Block block : e.getBlocks()) {
            if (cancelBlockEventByFlag(e, block, VBlockFlag.DENY_BLOCK_PISTON_EVENT))
                return;
        }
    }

    @EventHandler
    public void blockBurnEvent(BlockBurnEvent e) {
        plugin.consoleMessage("&8[&eVBlockListener&8] &eChecking " + e.getClass().getSimpleName(), 1, true);
        cancelBlockEventByFlag(e, VBlockFlag.DENY_BLOCK_BURN_EVENT);
    }
}
