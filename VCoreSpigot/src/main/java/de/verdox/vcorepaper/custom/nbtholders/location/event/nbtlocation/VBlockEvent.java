/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbtholders.location.event.nbtlocation;

import de.verdox.vcorepaper.custom.block.VBlock;
import de.verdox.vcorepaper.custom.events.VCoreHybridEvent;

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
