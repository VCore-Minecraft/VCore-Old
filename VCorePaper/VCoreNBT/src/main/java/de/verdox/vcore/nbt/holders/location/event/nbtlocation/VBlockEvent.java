/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nbt.holders.location.event.nbtlocation;

import de.verdox.vcore.events.VCoreHybridEvent;
import de.verdox.vcore.nbt.block.VBlock;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.08.2021 23:07
 */
public abstract class VBlockEvent extends VCoreHybridEvent {

    private final VBlock<?, ?, ?> vBlock;
    private boolean cancelled;

    public VBlockEvent(VBlock<?, ?, ?> vBlock) {
        this.vBlock = vBlock;
    }

    public VBlock<?, ?, ?> getVBlock() {
        return vBlock;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}