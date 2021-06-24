/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.data.datatypes.serializables.references.collections;

import de.verdox.vcore.data.datatypes.serializables.VCoreSerializableJson;
import de.verdox.vcore.data.datatypes.serializables.references.VCoreBsonReference;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.06.2021 00:00
 */
public class ListBsonReference<T> extends VCoreBsonReference<ArrayList<T>> {
    public ListBsonReference(@Nonnull VCoreSerializableJson vCoreSerializableJson, @Nonnull String fieldName) {
        super(vCoreSerializableJson, fieldName);
        if(!isFieldSet())
            setValue(new ArrayList<>());
    }

    @Override
    public ArrayList<T> defaultValue() {
        return new ArrayList<>();
    }
}
