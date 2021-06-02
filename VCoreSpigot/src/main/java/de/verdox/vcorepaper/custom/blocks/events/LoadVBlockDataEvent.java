package de.verdox.vcorepaper.custom.blocks.events;

import de.verdox.vcorepaper.custom.blocks.VBlock;

public class LoadVBlockDataEvent extends VBlockAsyncDataEvent{
    public LoadVBlockDataEvent(VBlock vBlock) {
        super(vBlock);
    }
}
