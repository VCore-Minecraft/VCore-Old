package de.verdox.vcorepaper.custom;

import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.annotation.NBTIdentifier;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 *
 * @param <S> DataHolder (e.g. ItemStack, Entity)
 * @param <T> CustomDataType (e.g. ItemCustomData)
 * @param <R> CustomDataHolder (e.g. VCoreItem)
 */

public abstract class CustomDataManager<S,T extends CustomData<?>, R extends CustomDataHolder<S,?,?>> {

    private final VCorePaper vCorePaper;
    protected final Map<String, Class<? extends T>> customDataCacheByString;
    protected final Map<Class<? extends T>, String> customDataCacheByClass;

    public CustomDataManager(VCorePaper vCorePaper){
        this.vCorePaper = vCorePaper;
        customDataCacheByString = new ConcurrentHashMap<>();
        customDataCacheByClass = new ConcurrentHashMap<>();
    }

    public abstract <U extends R> U wrap(Class<? extends U> type, S inputObject);
    public abstract <U extends R> U convertTo(Class<? extends U> type, R customData);

    public final void registerData(Class<? extends T> customDataClass){
        String nbtKey = findNBTKey(customDataClass);
        if(nbtKey == null)
            throw new IllegalStateException("Your CustomData Class "+getClass().getCanonicalName()+" needs to have the NBTIdentifier Annotation set!");
        customDataCacheByString.put(nbtKey,customDataClass);
        customDataCacheByClass.put(customDataClass,nbtKey);
        getVCorePaper().consoleMessage("&eRegistering CustomData&7: &b"+customDataClass.getSimpleName()+" &8[&b"+nbtKey+"&8]",true);
    }

    public T getDataType (String nbtKey){
        if(!customDataCacheByString.containsKey(nbtKey))
            return null;
        return instantiateCustomData(customDataCacheByString.get(nbtKey));
    }

    public Class<? extends T> getDataTypeClass(String nbtKey){
        if(!customDataCacheByString.containsKey(nbtKey))
            return null;
        return customDataCacheByString.get(nbtKey);
    }

    public boolean exists(String nbtKey){
        return customDataCacheByString.containsKey(nbtKey.toLowerCase());
    }

    public List<String> getNBTKeys(){
        return customDataCacheByString.keySet().stream().sorted().collect(Collectors.toList());
    }

    protected abstract T instantiateCustomData(Class<? extends T> dataClass);

    public VCorePaper getVCorePaper() {
        return vCorePaper;
    }

    public String getNBTKey(Class<?> classType){
        return customDataCacheByClass.get(classType);
    }

    public String findNBTKey(Class<?> classType){
        NBTIdentifier nbtIdentifier = classType.getAnnotation(NBTIdentifier.class);
        if(nbtIdentifier == null)
            throw new IllegalStateException("Your CustomData Class "+getClass().getCanonicalName()+" needs to have the NBTIdentifier Annotation set!");
        return nbtIdentifier.nbtKey().toLowerCase();
    }
}
