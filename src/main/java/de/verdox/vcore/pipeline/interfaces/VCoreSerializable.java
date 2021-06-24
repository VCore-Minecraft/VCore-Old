/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.pipeline.interfaces;

import de.verdox.vcore.pipeline.annotations.VCorePersistentData;
import de.verdox.vcore.util.VCoreUtil;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 00:48
 */
public interface VCoreSerializable {
    static Set<String> getPersistentDataFieldNames(Class<? extends VCoreSerializable> vCoreDataClass){
        return Arrays.stream(vCoreDataClass.getDeclaredFields())
                .filter(field -> field.getAnnotation(VCorePersistentData.class) != null)
                .map(Field::getName)
                .collect(Collectors.toSet());
    }

    static Set<Field> getPersistentDataFields(Class<? extends VCoreSerializable> vCoreDataClass){
        return Arrays.stream(vCoreDataClass.getDeclaredFields())
                .filter(field -> field.getAnnotation(VCorePersistentData.class) != null)
                .collect(Collectors.toSet());
    }

    default Map<String, Object> serialize(){
        Map<String, Object> serializedData = new HashMap<>();
        getPersistentDataFieldNames(getClass()).forEach(dataKey -> {
            try {
                Field field = getClass().getDeclaredField(dataKey);
                field.setAccessible(true);
                if(field.get(this) == null)
                    return;
                serializedData.put(field.getName(), field.get(this));
            } catch (NoSuchFieldException | IllegalAccessException e) { e.printStackTrace(); }
        });
        return serializedData;
    }

    default void deserialize(Map<String, Object> serializedData){
        serializedData.forEach((key, value) -> {
            if(key.equals("objectUUID"))
                return;
            if(key.equals("_id"))
                return;
            if(value == null)
                return;
            try {
                Field field = getClass().getDeclaredField(key);
                field.setAccessible(true);
                if(!field.getType().isPrimitive()){
                    try{
                        field.set(this, VCoreUtil.getTypeUtil().castData(value,field.getType()));
                    }
                    catch (ClassCastException e){
                        field.set(this,value);
                    }
                }
                else
                    field.set(this, value);
            } catch (IllegalAccessException | NoSuchFieldException e) { e.printStackTrace(); }
        });
    }
}
