/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.primitives;

import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.VCoreSerializableJson;
import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.VCoreBsonReference;

import javax.annotation.Nonnull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 23:59
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
