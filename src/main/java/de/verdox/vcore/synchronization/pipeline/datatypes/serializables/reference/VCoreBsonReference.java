/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference;

import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.VCoreSerializableJson;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 23:56
 */
public abstract class VCoreBsonReference<T> implements Serializable {
    private final VCoreSerializableJson vCoreSerializableJson;
    private final String fieldName;

    public VCoreBsonReference(@Nonnull VCoreSerializableJson vCoreSerializableJson, @Nonnull String fieldName){
        this.vCoreSerializableJson = vCoreSerializableJson;
        this.fieldName = fieldName;
    }

    public T getValue(){
        if(!vCoreSerializableJson.containsKey(fieldName)) {
            setValue(defaultValue());
            return defaultValue();
        }
        return (T) vCoreSerializableJson.get(fieldName);
    }

    public boolean isFieldSet(){
        return vCoreSerializableJson.containsKey(fieldName);
    }

    public T orElse(T elseValue){
        if(!vCoreSerializableJson.containsKey(fieldName))
            return elseValue;
        return getValue();
    }

    public VCoreBsonReference<T> setValue(@Nonnull T value){
        vCoreSerializableJson.put(fieldName, value);
        return this;
    }

    public String getFieldName() {
        return fieldName;
    }

    public abstract T defaultValue();
}
