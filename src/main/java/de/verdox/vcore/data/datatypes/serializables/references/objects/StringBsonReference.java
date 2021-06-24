/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.data.datatypes.serializables.references.objects;

import de.verdox.vcore.data.datatypes.serializables.VCoreSerializableJson;
import de.verdox.vcore.data.datatypes.serializables.references.VCoreBsonReference;

import javax.annotation.Nonnull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 20.06.2021 22:38
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
