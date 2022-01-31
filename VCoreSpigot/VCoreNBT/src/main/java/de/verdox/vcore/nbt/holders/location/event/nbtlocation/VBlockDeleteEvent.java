/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nbt.holders.location.event.nbtlocation;

import de.verdox.vcore.nbt.block.VBlock;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.08.2021 23:08
 */
public class VBlockDeleteEvent extends VBlockEvent {
    public VBlockDeleteEvent(VBlock<?, ?, ?> vBlock) {
        super(vBlock);
    }
}
