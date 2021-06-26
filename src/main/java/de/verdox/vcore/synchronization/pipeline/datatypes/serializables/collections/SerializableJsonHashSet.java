/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.datatypes.serializables.collections;

import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.VCoreSerializableJson;
import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.collections.SetBsonReference;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 23:55
 */
public class SerializableJsonHashSet<T> extends VCoreSerializableJson {
    public SetBsonReference<T> getSet(){
        return new SetBsonReference<>(this, "set");
    }
}
