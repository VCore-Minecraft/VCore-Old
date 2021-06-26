/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.objects;

import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.VCoreSerializableJson;
import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.VCoreBsonReference;

import javax.annotation.Nonnull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 23:58
 */
public class StringBsonReference extends VCoreBsonReference<String> {
    public StringBsonReference(@Nonnull VCoreSerializableJson vCoreSerializableJson, @Nonnull String fieldName) {
        super(vCoreSerializableJson, fieldName);
    }

    @Override
    public String defaultValue() {
        return null;
    }
}
