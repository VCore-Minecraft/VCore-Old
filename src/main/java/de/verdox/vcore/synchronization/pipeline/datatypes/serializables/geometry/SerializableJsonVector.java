/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.datatypes.serializables.geometry;

import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.VCoreSerializableJson;
import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.VCoreBsonReference;
import de.verdox.vcore.synchronization.pipeline.datatypes.serializables.reference.primitives.numbers.DoubleBsonReference;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 23:55
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
