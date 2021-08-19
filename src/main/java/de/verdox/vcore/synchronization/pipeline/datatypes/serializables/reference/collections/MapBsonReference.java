/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.collections;

import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.VCoreDataReference;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 30.06.2021 23:38
 */
public class MapBsonReference<K, V> extends VCoreDataReference<Map<K, V>> {
    public MapBsonReference(@Nonnull Map<String, Object> data, @Nonnull String fieldName) {
        super(data, fieldName);
    }

    @Override
    public Map<K, V> defaultValue() {
        return new HashMap<>();
    }
}
