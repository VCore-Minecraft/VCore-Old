/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbtholders.block.event;

import de.verdox.vcorepaper.custom.block.VBlock;
import de.verdox.vcorepaper.custom.nbtholders.block.NBTBlock;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.08.2021 23:08
 */
public class NBTBlockDeleteEvent extends NBTBlockEvent{
    public NBTBlockDeleteEvent(NBTBlock nbtBlock) {
        super(nbtBlock);
    }
}
