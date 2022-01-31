/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nbt.block.data;

import de.verdox.vcore.nbt.CustomData;

import java.util.List;

public abstract class VBlockCustomData<T> extends CustomData<T> {
    @Override
    public List<String> asLabel(String valueAsString) {
        return List.of(valueAsString);
    }
}
