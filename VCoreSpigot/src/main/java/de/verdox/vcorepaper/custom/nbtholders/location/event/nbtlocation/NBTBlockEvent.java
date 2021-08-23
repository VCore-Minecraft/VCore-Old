/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbtholders.location.event.nbtlocation;

import de.verdox.vcorepaper.custom.events.VCoreHybridEvent;
import de.verdox.vcorepaper.custom.nbtholders.location.NBTLocation;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.08.2021 23:07
 */
public abstract class NBTBlockEvent extends VCoreHybridEvent {

    private final NBTLocation nbtLocation;
    private boolean cancelled;

    public NBTBlockEvent(NBTLocation nbtLocation) {
        this.nbtLocation = nbtLocation;
    }

    public NBTLocation getNbtBlock() {
        return nbtLocation;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
