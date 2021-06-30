/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.pipeline.datatypes.serializables;

import org.bson.Document;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 23:54
 */
public abstract class VCoreSerializableJson extends Document {
    public VCoreSerializableJson(){}
    public static <T extends VCoreSerializableJson> T wrap(Class<? extends T> type, Document document){
        try {
            Constructor<? extends T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            T serializable = constructor.newInstance();
            serializable.putAll(document);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}