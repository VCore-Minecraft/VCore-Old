package de.verdox.vcorepaper.custom.blocks.events;

import de.verdox.vcorepaper.custom.blocks.VBlock;
import de.verdox.vcorepaper.custom.events.VCoreAsyncEvent;

public class VBlockAsyncDataEvent extends VCoreAsyncEvent {

    private VBlock vBlock;

    public VBlockAsyncDataEvent(VBlock vBlock){
        this.vBlock = vBlock;
    }

    public VBlock getvBlock() {
        return vBlock;
    }
}
