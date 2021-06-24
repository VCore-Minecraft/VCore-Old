/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.data.datatypes.serializables.collections;

import de.verdox.vcore.data.datatypes.serializables.VCoreSerializableJson;
import de.verdox.vcore.data.datatypes.serializables.references.collections.SetBsonReference;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.06.2021 00:08
 */
public class SerializableJsonHashSet<T> extends VCoreSerializableJson {

    public SetBsonReference<T> getSet(){
        return new SetBsonReference<>(this, "set");
    }
}
