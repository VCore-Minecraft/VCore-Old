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
 * @date 20.06.2021 22:39
 */
public class DoubleBsonReference extends VCoreBsonReference<Double> {
    public DoubleBsonReference(@Nonnull VCoreSerializable vCoreSerializable, @Nonnull String fieldName) {
        super(vCoreSerializable, fieldName);
    }

    @Override
    public Double defaultValue() {
        return 0d;
    }
}
