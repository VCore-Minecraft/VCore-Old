/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.data.datatypes.serializables.references.primitives;

import de.verdox.vcore.data.datatypes.serializables.VCoreSerializableJson;
import de.verdox.vcore.data.datatypes.serializables.references.VCoreBsonReference;

import javax.annotation.Nonnull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 20.06.2021 22:39
 */
public class BooleanBsonReference extends VCoreBsonReference<Boolean> {
    public BooleanBsonReference(@Nonnull VCoreSerializableJson vCoreSerializableJson, @Nonnull String fieldName) {
        super(vCoreSerializableJson, fieldName);
    }

    @Override
    public Boolean defaultValue() {
        return null;
    }
}
