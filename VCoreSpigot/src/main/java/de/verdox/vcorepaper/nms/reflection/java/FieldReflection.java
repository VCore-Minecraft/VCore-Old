/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms.reflection.java;

import java.lang.reflect.Field;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.06.2021 23:38
 */
public class FieldReflection {

    /**
     * Gets a Field via reflective access
     *
     * @param fieldName      The name of the field (Case sensitive)
     * @param reflectedClass The class to perform the action on
     * @return The found field
     */
    public static <T> ReferenceField<T> getField(Class<?> reflectedClass, String fieldName, Class<T> fieldType) {
        try {
            Field field = reflectedClass.getDeclaredField(fieldName);
            return new ReferenceField<>(field);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static class ReferenceField<T> {

        private final Field field;
        private Object accessor = null;

        ReferenceField(Field field) {
            this.field = field;
        }

        public ReferenceField<T> of(Object accessor) {
            this.accessor = accessor;
            return this;
        }

        /**
         * Reading a Fields value via Reflection
         *
         * @return The Value of the Field
         */
        public T readField() {
            try {
                field.setAccessible(true);
                return (T) field.get(accessor);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * Setting a Field via Reflection
         *
         * @param value The Value the field will get
         */
        public ReferenceField<T> setField(T value) {
            try {
                field.setAccessible(true);
                field.set(accessor, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return this;
        }

    }

}
