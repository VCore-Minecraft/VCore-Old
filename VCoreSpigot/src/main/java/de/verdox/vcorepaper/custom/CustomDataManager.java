package de.verdox.vcorepaper.custom;

import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.annotation.NBTIdentifier;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
    protected final Map<String, Class<? extends T>> customDataCache;

    public CustomDataManager(VCorePaper vCorePaper){
        this.vCorePaper = vCorePaper;
        customDataCache = new ConcurrentHashMap<>();
    }

    public abstract R wrap(Class<? extends R> type, S inputObject);

    public final void registerData(Class<? extends T> customDataClass){
        String nbtKey = findNBTKey(customDataClass);
        if(nbtKey == null)
            throw new IllegalStateException("Your CustomData Class "+getClass().getCanonicalName()+" needs to have the NBTIdentifier Annotation set!");
        customDataCache.put(nbtKey,customDataClass);
        getVCorePaper().consoleMessage("&eRegistering CustomData&7: &b"+customDataClass.getSimpleName()+" &8[&b"+nbtKey+"&8]",true);
    }

    public T getDataType (String nbtKey){
        if(!customDataCache.containsKey(nbtKey))
            throw new NullPointerException("customData with nbtKey "+nbtKey+" does not contain!");
        return instantiateCustomData(customDataCache.get(nbtKey));
    }

    public Class<? extends T> getDataTypeClass(String nbtKey){
        if(!customDataCache.containsKey(nbtKey))
            throw new NullPointerException("customData with nbtKey "+nbtKey+" does not contain!");
        return customDataCache.get(nbtKey);
    }

    public boolean exists(String nbtKey){
        return customDataCache.containsKey(nbtKey.toLowerCase());
    }

    public List<String> getNBTKeys(){
        return customDataCache.keySet().stream().sorted().collect(Collectors.toList());
    }

    protected abstract T instantiateCustomData(Class<? extends T> dataClass);

    public VCorePaper getVCorePaper() {
        return vCorePaper;
    }

    public String findNBTKey(Class<? extends T> classType){
        NBTIdentifier nbtIdentifier = classType.getAnnotation(NBTIdentifier.class);
        if(nbtIdentifier == null)
            throw new IllegalStateException("Your CustomData Class "+getClass().getCanonicalName()+" needs to have the NBTIdentifier Annotation set!");
        return nbtIdentifier.nbtKey().toLowerCase();
    }
}
