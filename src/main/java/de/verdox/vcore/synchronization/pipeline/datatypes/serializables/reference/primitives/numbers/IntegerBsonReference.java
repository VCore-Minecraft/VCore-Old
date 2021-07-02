/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.primitives.numbers;

import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.VCoreDataReference;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 23:59
 */
public class IntegerBsonReference extends VCoreDataReference<Integer> {
    public IntegerBsonReference(@Nonnull Map<String, Object> data, @Nonnull String fieldName) {
        super(data, fieldName);
    }

    @Override
    public Integer defaultValue() {
        return 0;
    }
}
