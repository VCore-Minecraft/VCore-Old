/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.collections;

import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.VCoreDataReference;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 23:57
 */
public class ListBsonReference<T> extends VCoreDataReference<List<T>> {
    public ListBsonReference(@Nonnull Map<String, Object> data, @Nonnull String fieldName) {
        super(data, fieldName);
        if (!isFieldSet())
            setValue(new ArrayList<>());
    }

    public void addData(T object, boolean allowDuplicates) {
        if (allowDuplicates && getValue().contains(object))
            return;
        getValue().add(object);
    }

    public boolean exist(T object) {
        return getValue().contains(object);
    }

    public void remove(T object) {
        getValue().remove(object);
    }

    @Override
    public ArrayList<T> defaultValue() {
        return new ArrayList<>();
    }
}
