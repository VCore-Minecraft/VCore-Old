package de.verdox.vcorepaper.custom.events.callbacks;

import de.verdox.vcorepaper.custom.blocks.VBlock;
import de.verdox.vcorepaper.custom.items.VCoreItem;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public interface BlockInteractCallback extends EventBlockCallback{
    @Override
    void blockCallback(Player interactPlayer, Action action, VCoreItem itemInHand, VBlock vBlock, boolean cancelled);
}
