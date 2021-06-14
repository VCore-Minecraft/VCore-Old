package de.verdox.vcore.util;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.inventory.ItemStack;

import java.nio.ByteBuffer;
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
        else if(type.equals(Float.class))
            return type.cast(Float.parseFloat(data.toString()));
        else if(type.equals(String.class))
            return type.cast(data.toString());
        else if(type.equals(UUID.class))
            return type.cast(UUID.fromString(data.toString()));
        else if(type.equals(Long.class))
            return type.cast(Long.parseLong(data.toString()));
        return type.cast(data);
    }

    public String makeSerializable(Object data){
        if(data == null)
            return null;
        if(data instanceof UUID)
            return data.toString();
        return data.toString();
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

    public String uuidToBase64(String str) {
        UUID uuid = UUID.fromString(str);
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return Base64.encodeBase64URLSafeString(bb.array());
    }

    public String uuidFromBase64(String str) {
        byte[] bytes = Base64.decodeBase64(str);
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        UUID uuid = new UUID(bb.getLong(), bb.getLong());
        return uuid.toString();
    }

}
