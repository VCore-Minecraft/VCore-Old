/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Map;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 23:56
 */
public abstract class VCoreDataReference<T> implements Serializable {
    private final Map<String, Object> data;
    private final String fieldName;

    public VCoreDataReference(@Nonnull Map<String, Object> data, @Nonnull String fieldName){
        this.data = data;
        this.fieldName = fieldName;
    }

    public T getValue(){
        if(!data.containsKey(fieldName)) {
            setValue(defaultValue());
            return defaultValue();
        }
        return (T) data.get(fieldName);
    }

    public boolean isFieldSet(){
        return data.containsKey(fieldName);
    }

    public T orElse(T elseValue){
        if(!data.containsKey(fieldName))
            return elseValue;
        return getValue();
    }

    public VCoreDataReference<T> setValue(@Nonnull T value){
        data.put(fieldName, value);
        return this;
    }

    public String getFieldName() {
        return fieldName;
    }

    public abstract T defaultValue();
}
