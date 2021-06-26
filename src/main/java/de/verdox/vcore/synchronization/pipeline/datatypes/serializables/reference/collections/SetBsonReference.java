/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.collections;

import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.VCoreSerializableJson;
import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.VCoreBsonReference;

import javax.annotation.Nonnull;
import java.util.HashSet;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 23:57
 */
public class SetBsonReference<T> extends VCoreBsonReference<HashSet<T>> {
    public SetBsonReference(@Nonnull VCoreSerializableJson vCoreSerializableJson, @Nonnull String fieldName) {
        super(vCoreSerializableJson, fieldName);
        if(!isFieldSet())
            setValue(new HashSet<>());
    }

    @Override
    public HashSet<T> defaultValue() {
        return new HashSet<>();
    }
}
