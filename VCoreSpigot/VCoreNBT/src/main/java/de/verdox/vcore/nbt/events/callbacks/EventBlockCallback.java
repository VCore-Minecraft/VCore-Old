/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nbt.events.callbacks;

import de.verdox.vcore.nbt.block.VBlock;
import de.verdox.vcore.nbt.items.VCoreItem;
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
