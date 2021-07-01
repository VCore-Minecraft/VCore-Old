/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.collections;

import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.VCoreSerializableJson;
import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.VCoreDataReference;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 23:57
 */
public class SetBsonReference<T> extends VCoreDataReference<Set<T>> {

    public SetBsonReference(@Nonnull Map<String, Object> data, @Nonnull String fieldName) {
        super(data, fieldName);
        if(!isFieldSet())
            setValue(new HashSet<>());
    }

    @Override
    public Set<T> defaultValue() {
        return new HashSet<>();
    }
}
