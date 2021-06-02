package de.verdox.vcore.util;

import java.util.UUID;

public class TypeUtil {

    TypeUtil(){}

    public <T> T castData(Object data, Class<T> type){
        if(data == null)
            return null;
        if(type.equals(Boolean.class))
            return type.cast(Boolean.parseBoolean(data.toString()));
        else if(type.equals(Integer.class))
            return type.cast(Integer.parseInt(data.toString()));
        else if(type.equals(Double.class))
            return type.cast(Double.parseDouble(data.toString()));
        else if(type.equals(String.class))
            return type.cast(data.toString());
        else if(type.equals(UUID.class))
            return type.cast(UUID.fromString(data.toString()));
        else if(type.equals(Long.class))
            return type.cast(Long.parseLong(data.toString()));
        return type.cast(data);
    }

    public int parseInt(String input){
        try{
            return Integer.parseInt(input);
        }
        catch (NumberFormatException e){
            return Integer.MIN_VALUE;
        }
    }

    public float parseFloat(String input){
        try{
            return Float.parseFloat(input);
        }
        catch (NumberFormatException e){
            return Float.MIN_VALUE;
        }
    }
}
