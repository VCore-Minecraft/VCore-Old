/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.old_blocks.events;

import de.verdox.vcorepaper.custom.old_blocks.VBlock;

/**
 * Called when a VBlockSaveFile is loaded from storage
 */
public class LoadVBlockDataEvent extends VBlockAsyncDataEvent {
    public LoadVBlockDataEvent(VBlock vBlock) {
        super(vBlock);
    }
}
