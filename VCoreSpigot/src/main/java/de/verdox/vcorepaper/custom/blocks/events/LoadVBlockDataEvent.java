package de.verdox.vcorepaper.custom.blocks.events;

import de.verdox.vcorepaper.custom.blocks.VBlock;

/**
 * Called when a VBlockSaveFile is loaded from storage
 */
public class LoadVBlockDataEvent extends VBlockAsyncDataEvent{
    public LoadVBlockDataEvent(VBlock vBlock) {
        super(vBlock);
    }
}
