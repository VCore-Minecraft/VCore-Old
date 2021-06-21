/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.data.serializables.references.primitives.numbers;

import de.verdox.vcore.data.serializables.VCoreSerializable;
import de.verdox.vcore.data.serializables.references.VCoreBsonReference;

import javax.annotation.Nonnull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 20.06.2021 22:38
 */
public class IntegerBsonReference extends VCoreBsonReference<Integer> {
    public IntegerBsonReference(@Nonnull VCoreSerializable vCoreSerializable, @Nonnull String fieldName) {
        super(vCoreSerializable, fieldName);
    }

    @Override
    public Integer defaultValue() {
        return 0;
    }
}
