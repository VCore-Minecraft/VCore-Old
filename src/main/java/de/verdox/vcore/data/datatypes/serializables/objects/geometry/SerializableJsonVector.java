/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.data.datatypes.serializables.objects.geometry;

import de.verdox.vcore.data.datatypes.serializables.VCoreSerializableJson;
import de.verdox.vcore.data.datatypes.serializables.references.VCoreBsonReference;
import de.verdox.vcore.data.datatypes.serializables.references.primitives.numbers.DoubleBsonReference;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 20.06.2021 20:44
 */
public class SerializableJsonVector extends VCoreSerializableJson {
    public VCoreBsonReference<Double> getXReference(){
        return new DoubleBsonReference(this, "x");
    }

    public VCoreBsonReference<Double> getYReference(){
        return new DoubleBsonReference(this, "y");
    }

    public VCoreBsonReference<Double> getZReference(){
        return new DoubleBsonReference(this, "z");
    }
}
