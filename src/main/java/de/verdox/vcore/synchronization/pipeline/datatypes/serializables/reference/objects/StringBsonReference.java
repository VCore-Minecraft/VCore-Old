/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.objects;

import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.VCoreFieldReference;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 23:58
 */
public class StringBsonReference extends VCoreFieldReference<String> {

    public StringBsonReference(@NotNull Map<String, Object> data, @NotNull String fieldName) {
        super(data, fieldName);
    }

    @Override
    public String defaultValue() {
        return null;
    }
}
