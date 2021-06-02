package de.verdox.vcorepaper.custom.blocks.events;

import de.verdox.vcorepaper.custom.blocks.VBlock;

public class UnloadVBlockDataEvent extends VBlockAsyncDataEvent{
    public UnloadVBlockDataEvent(VBlock vBlock) {
        super(vBlock);
    }
}
