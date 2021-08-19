/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.old_blocks.events;

import de.verdox.vcorepaper.custom.old_blocks.VBlock;
import de.verdox.vcorepaper.custom.events.VCoreAsyncEvent;

public class VBlockAsyncDataEvent extends VCoreAsyncEvent {

    private VBlock vBlock;

    public VBlockAsyncDataEvent(VBlock vBlock) {
        this.vBlock = vBlock;
    }

    public VBlock getVBlock() {
        return vBlock;
    }
}
