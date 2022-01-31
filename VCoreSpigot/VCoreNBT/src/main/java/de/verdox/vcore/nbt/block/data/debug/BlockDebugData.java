/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nbt.block.data.debug;

import de.verdox.vcore.nbt.block.data.VBlockCustomData;
import de.verdox.vcorepaper.custom.annotation.NBTIdentifier;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 07.08.2021 02:12
 */
@NBTIdentifier(nbtKey = "debugKey")
public class BlockDebugData extends VBlockCustomData<Long> {
    @NotNull
    @Override
    public Class<Long> getTypeClass() {
        return Long.class;
    }

    @Override
    public Long defaultValue() {
        return System.currentTimeMillis();
    }
}
