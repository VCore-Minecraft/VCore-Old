package de.verdox.vcorepaper.custom.events.callbacks;

import de.verdox.vcorepaper.custom.blocks.VBlock;
import de.verdox.vcorepaper.custom.items.VCoreItem;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface EventBlockCallback {
    void blockCallback(Player placer, VCoreItem itemInHand, VBlock vBlock, boolean cancelled);
}
