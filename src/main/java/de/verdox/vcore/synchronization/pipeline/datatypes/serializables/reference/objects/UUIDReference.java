/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.objects;

import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.VCoreFieldReference;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 18.09.2021 22:18
 */
public class UUIDReference extends VCoreFieldReference<UUID> {

    private final StringBsonReference stringReference;

    public UUIDReference(@NotNull Map<String, Object> data, @NotNull String fieldName) {
        super(data, fieldName);
        stringReference = new StringBsonReference(data, fieldName);
    }

    @Override
    public UUID orElse(UUID elseValue) {
        UUID parsed = parseUUIDSafely(stringReference.orElse(null));
        if (parsed != null)
            return parsed;
        else
            return elseValue;
    }

    @Override
    public VCoreFieldReference<UUID> setValue(@NotNull UUID value) {
        stringReference.setValue(value.toString());
        return this;
    }

    @Override
    public UUID getValue() {
        return parseUUIDSafely(stringReference.getValue());
    }

    @Override
    public boolean isFieldSet() {
        return stringReference.isFieldSet();
    }

    @Override
    public UUID defaultValue() {
        return null;
    }

    private UUID parseUUIDSafely(String value) {
        if (value == null)
            return null;
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
