/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.data.serializables.references.primitives;

import de.verdox.vcore.data.serializables.VCoreSerializable;
import de.verdox.vcore.data.serializables.references.VCoreBsonReference;

import javax.annotation.Nonnull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 20.06.2021 22:40
 */
public class CharBsonReference extends VCoreBsonReference<Character> {
    public CharBsonReference(@Nonnull VCoreSerializable vCoreSerializable, @Nonnull String fieldName) {
        super(vCoreSerializable, fieldName);
    }

    @Override
    public Character defaultValue() {
        return '\0';
    }
}