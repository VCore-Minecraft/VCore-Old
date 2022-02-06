/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nms.api.reflection.java;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 22.06.2021 01:33
 */
public class MethodReflection {

    public static <T> ReferenceMethod<T> findMethod(Class<?> reflectedClass, String name, Class<T> returnType, Class<?>... paramTypes) {
        try {
            new ReferenceMethod<>(reflectedClass.getMethod(name, paramTypes));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static class ReferenceMethod<T> {

        private final Method method;
        private Object accessor;

        ReferenceMethod(Method method) {
            this.method = method;
        }

        public ReferenceMethod<T> of(Object accessor) {
            this.accessor = accessor;
            return this;
        }

        public T invoke(Object... params) {
            try {
                return (T) method.invoke(accessor, params);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }

    }

}
