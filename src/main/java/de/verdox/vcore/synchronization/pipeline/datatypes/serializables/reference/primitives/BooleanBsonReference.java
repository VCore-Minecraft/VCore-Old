/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.primitives;

import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.VCoreDataReference;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 23:59
 */
public class BooleanBsonReference extends VCoreDataReference<Boolean> {
    public BooleanBsonReference(@Nonnull Map<String, Object> data, @Nonnull String fieldName) {
        super(data, fieldName);
    }

    @Override
    public Boolean defaultValue() {
        return false;
    }
}
