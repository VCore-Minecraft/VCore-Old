package de.verdox.vcorepaper.custom;

import de.verdox.vcorepaper.custom.annotation.NBTIdentifier;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * @param <T> Data to be stored
 */
public abstract class CustomData <T> {
    public CustomData(){}

    public abstract List<String> asLabel(String valueAsString);

    public T findInDataHolder(CustomDataHolder<?,?,?> customDataHolder){
        String nbtKey = getNBTKey();
        if(!customDataHolder.getNBTCompound().hasKey(nbtKey))
            return defaultValue();
        if(getTypeClass() == null)
            throw new NullPointerException("Can't return null!");
        return customDataHolder.getNBTCompound().getObject(nbtKey,getTypeClass());
    }

    public void storeCustomData(CustomDataHolder<?,?,?> customDataHolder, T data){
        customDataHolder.getNBTCompound().setObject(getNBTKey(),data);
    }

    public abstract Class<T> getTypeClass();
    public abstract T defaultValue();

    public String getNBTKey(){
        NBTIdentifier nbtIdentifier = getClass().getAnnotation(NBTIdentifier.class);
        if(nbtIdentifier == null)
            throw new IllegalStateException("Your CustomData Class "+getClass().getCanonicalName()+" needs to have the NBTIdentifier Annotation set!");
        return nbtIdentifier.nbtKey().toLowerCase();
    }
}
