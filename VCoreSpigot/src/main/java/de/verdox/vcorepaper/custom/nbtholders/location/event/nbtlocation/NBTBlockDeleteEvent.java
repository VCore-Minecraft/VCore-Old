/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbtholders.location.event.nbtlocation;

import de.verdox.vcorepaper.custom.nbtholders.location.NBTLocation;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.08.2021 23:08
 */
public class NBTBlockDeleteEvent extends NBTBlockEvent {
    public NBTBlockDeleteEvent(NBTLocation nbtLocation) {
        super(nbtLocation);
    }
}