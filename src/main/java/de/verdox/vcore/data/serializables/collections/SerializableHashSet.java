/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.data.serializables.collections;

import de.verdox.vcore.data.serializables.VCoreSerializable;
import de.verdox.vcore.data.serializables.references.collections.SetBsonReference;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.06.2021 00:08
 */
public class SerializableHashSet<T> extends VCoreSerializable {

    public SetBsonReference<T> getSet(){
        return new SetBsonReference<>(this, "set");
    }
}
