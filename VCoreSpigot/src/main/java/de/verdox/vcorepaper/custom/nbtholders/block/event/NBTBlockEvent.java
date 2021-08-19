/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbtholders.block.event;

import de.verdox.vcorepaper.custom.events.VCoreHybridEvent;
import de.verdox.vcorepaper.custom.nbtholders.block.NBTBlock;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.08.2021 23:07
 */
public abstract class NBTBlockEvent extends VCoreHybridEvent {

    private final NBTBlock nbtBlock;
    private boolean cancelled;

    public NBTBlockEvent(NBTBlock nbtBlock) {
        this.nbtBlock = nbtBlock;
    }

    public NBTBlock getNbtBlock() {
        return nbtBlock;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
