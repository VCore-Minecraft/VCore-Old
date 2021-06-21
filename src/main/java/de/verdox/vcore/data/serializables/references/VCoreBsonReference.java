/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.data.serializables.references;

import de.verdox.vcore.data.serializables.VCoreSerializable;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 20.06.2021 20:46
 */
public abstract class VCoreBsonReference<T> implements Serializable {
    private final VCoreSerializable vCoreSerializable;
    private final String fieldName;

    public VCoreBsonReference(@Nonnull VCoreSerializable vCoreSerializable, @Nonnull String fieldName){
        this.vCoreSerializable = vCoreSerializable;
        this.fieldName = fieldName;
    }

    public T getValue(){
        if(!vCoreSerializable.containsKey(fieldName)) {
            setValue(defaultValue());
            return defaultValue();
        }
        return (T) vCoreSerializable.get(fieldName);
    }

    public boolean isFieldSet(){
        return vCoreSerializable.containsKey(fieldName);
    }

    public T orElse(T elseValue){
        if(!vCoreSerializable.containsKey(fieldName))
            return elseValue;
        return getValue();
    }

    public VCoreBsonReference<T> setValue(@Nonnull T value){
        vCoreSerializable.put(fieldName, value);
        return this;
    }

    public String getFieldName() {
        return fieldName;
    }

    public abstract T defaultValue();
}
