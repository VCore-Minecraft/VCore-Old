package de.verdox.vcorepaper.custom.blocks.events;

import de.verdox.vcorepaper.custom.blocks.VBlock;

/**
 * Called when a VBlockSaveFile is unloaded
 */
public class UnloadVBlockDataEvent extends VBlockAsyncDataEvent{
    public UnloadVBlockDataEvent(VBlock vBlock) {
        super(vBlock);
    }
}
