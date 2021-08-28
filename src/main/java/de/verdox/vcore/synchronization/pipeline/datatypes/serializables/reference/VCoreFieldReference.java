/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Map;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 23:56
 */
public abstract class VCoreFieldReference<T> implements Serializable {
    private final Map<String, Object> data;
    private final String fieldName;

    public VCoreFieldReference(@NotNull Map<String, Object> data, @NotNull String fieldName) {
        this.data = data;
        this.fieldName = fieldName;
    }

    public T getValue() {
        if (!data.containsKey(fieldName)) {
            setValue(defaultValue());
            return defaultValue();
        }
        return (T) data.get(fieldName);
    }

    public VCoreFieldReference<T> setValue(@NotNull T value) {
        data.put(fieldName, value);
        return this;
    }

    public boolean isFieldSet() {
        return data.containsKey(fieldName);
    }

    public T orElse(T elseValue) {
        if (!data.containsKey(fieldName))
            return elseValue;
        return getValue();
    }

    public String getFieldName() {
        return fieldName;
    }

    public abstract T defaultValue();
}
