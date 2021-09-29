/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.events.callbacks;

import de.verdox.vcorepaper.custom.nbt.block.VBlock;
import de.verdox.vcorepaper.custom.nbt.items.VCoreItem;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public interface EventBlockCallback {
    void blockCallback(Player placer, Action action, VCoreItem itemInHand, VBlock<?, ?, ?> vBlock, CallbackType callbackType);

    enum CallbackType {
        BREAK_BLOCK,
        PLACE_BLOCK,
        INTERACT_BLOCK,
    }
}
