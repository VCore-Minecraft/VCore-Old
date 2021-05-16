package de.verdox.vcorepaper.custom;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.verdox.vcorepaper.custom.nbtholders.NBTHolder;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 *
 * @param <S> DataHolder Class (e.g. ItemStack, Entity)
 * @param <N> NBTCompound (e.g. NBTItem, NBTEntity)
 */
public abstract class CustomDataHolder <S, N extends NBTHolder, C extends CustomDataManager<S,?,?>> {

    private final S dataHolder;
    private final C customDataManager;

    public CustomDataHolder(S dataHolder, C customDataManager){
        this.dataHolder = dataHolder;
        this.customDataManager = customDataManager;
    }

    protected abstract <T,R extends CustomData<T>> void onStoreData(Class<? extends R> customDataType, T value);

    public final <T, R extends CustomData<T>> CustomDataHolder<S,N,C> storeCustomData(Class<? extends R> customDataType, T value, Consumer<R> callback){
        R customData = instantiateData(customDataType);
        if(customData == null)
            throw new NullPointerException("CustomData could not be instantiated");
        customData.storeCustomData(this,value);
        onStoreData(customDataType,value);
        if(callback == null)
            return this;
        callback.accept(customData);
        return this;
    }

    public final <T,R extends CustomData<T>> T getCustomData(Class<? extends R> customDataClass){
        R customData = instantiateData(customDataClass);
        if(customData == null) {
            throw new NullPointerException("Could not instantiate: "+customDataClass);
        }
        return customData.findInDataHolder(this);
    }

    public Set<String> getCustomDataKeys(){
        return getNBTCompound().getKeys().parallelStream().filter(customDataManager::exists).collect(Collectors.toSet());
    }

    protected abstract <T,R extends CustomData<T>> R instantiateData(Class <? extends R> customDataType);

    public S getDataHolder() {
        return dataHolder;
    }

    public abstract N getNBTCompound();

    public C getCustomDataManager() {
        return customDataManager;
    }
}
