/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms.reflection.java;

import java.util.Objects;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.06.2021 23:47
 */
public class ClassReflection {

    public static ReferenceClass findClass(String name) {
        try {
            return new ReferenceClass(Class.forName(name));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ReferenceClass toReferenceClass(Class<?> classToReference) {
        return new ReferenceClass(classToReference);
    }

    public static class ReferenceClass {
        private final Class<?> reflectedClass;

        ReferenceClass(Class<?> reflectedClass) {
            this.reflectedClass = reflectedClass;
        }

        public Class<?> getReflectedClass() {
            return reflectedClass;
        }

        public boolean isInstance(Object object) {
            return reflectedClass.isInstance(object);
        }

        public <T> FieldReflection.ReferenceField<T> findField(String fieldName, Class<T> fieldType) {
            return FieldReflection.getField(reflectedClass, fieldName, fieldType);
        }

        public ConstructorReflection.ReferenceConstructor findConstructor(Class<?>... paramTypes) {
            return ConstructorReflection.findConstructor(reflectedClass, paramTypes);
        }

        public <T> MethodReflection.ReferenceMethod<T> fiendMethod(String name, Class<T> returnType, Class<?>... paramTypes) {
            return MethodReflection.findMethod(reflectedClass, name, returnType, paramTypes);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ReferenceClass)) return false;
            ReferenceClass that = (ReferenceClass) o;
            return Objects.equals(getReflectedClass(), that.getReflectedClass());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getReflectedClass());
        }
    }

}
