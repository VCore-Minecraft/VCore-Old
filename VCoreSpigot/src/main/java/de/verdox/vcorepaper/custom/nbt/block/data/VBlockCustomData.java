/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbt.block.data;

import de.verdox.vcorepaper.custom.nbt.CustomData;

import java.util.List;

public abstract class VBlockCustomData<T> extends CustomData<T> {
    @Override
    public List<String> asLabel(String valueAsString) {
        return List.of(valueAsString);
    }
}
