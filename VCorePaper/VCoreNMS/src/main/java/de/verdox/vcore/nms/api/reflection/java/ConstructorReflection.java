/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nms.api.reflection.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 22.06.2021 01:27
 */
public class ConstructorReflection {

    public static ReferenceConstructor findConstructor(Class<?> classToReflect, Class<?>... paramTypes) {
        try {
            return new ReferenceConstructor(classToReflect.getDeclaredConstructor(paramTypes));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public record ReferenceConstructor(Constructor<?> constructor) {

        public Object instantiate(Object... params) {
            try {
                return this.constructor.newInstance(params);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }


    }

}
