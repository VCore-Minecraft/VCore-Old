/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.data.serializables.references.collections;

import de.verdox.vcore.data.serializables.VCoreSerializable;
import de.verdox.vcore.data.serializables.references.VCoreBsonReference;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.06.2021 00:00
 */
public class ListBsonReference<T> extends VCoreBsonReference<ArrayList<T>> {
    public ListBsonReference(@Nonnull VCoreSerializable vCoreSerializable, @Nonnull String fieldName) {
        super(vCoreSerializable, fieldName);
        if(!isFieldSet())
            setValue(new ArrayList<>());
    }

    @Override
    public ArrayList<T> defaultValue() {
        return new ArrayList<>();
    }
}
