package de.verdox.vcorepaper.custom;

import de.verdox.vcorepaper.custom.items.VCoreItem;
import de.verdox.vcorepaper.custom.nbtholders.NBTHolder;

import java.util.Objects;
import java.util.Set;
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
        if(!customDataManager.exists(customData.getNBTKey()))
            throw new IllegalStateException("CustomDataClass "+customDataType+" has not yet been registered in your plugin!");
        if(value == null)
            throw new NullPointerException("Can't store value null to dataHolder");
        customData.storeCustomData(this,value);
        onStoreData(customDataType,value);
        if(callback == null)
            return this;
        callback.accept(customData);
        return this;
    }

    public final <T,R extends CustomData<T>> T getCustomData(Class<? extends R> customDataClass){
        if(!containsCustomData(customDataClass))
            return null;
        R customData = instantiateData(customDataClass);
        if(customData == null)
            throw new NullPointerException("Could not instantiate: "+customDataClass);
        if(!customDataManager.exists(customData.getNBTKey()))
            throw new IllegalStateException("CustomDataClass "+customDataClass+" has not yet been registered in your plugin!");
        return customData.findInDataHolder(this);
    }

    public <T,R extends CustomData<T>> boolean containsCustomData(Class<? extends R> customDataClass){
        String nbtKey = customDataManager.getNBTKey(customDataClass);
        return getNBTCompound().hasKey(nbtKey);
    }

    public Set<String> getCustomDataKeys(){
        return getNBTCompound().getKeys().parallelStream().collect(Collectors.toSet());
    }

    protected abstract <T,R extends CustomData<T>> R instantiateData(Class <? extends R> customDataType);

    public S getDataHolder() {
        return dataHolder;
    }

    public abstract N getNBTCompound();

    public C getCustomDataManager() {
        return customDataManager;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomDataHolder)) return false;
        CustomDataHolder<?, ?, ?> that = (CustomDataHolder<?, ?, ?>) o;
        return Objects.equals(getDataHolder(), that.getDataHolder());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDataHolder());
    }

    @Override
    public String toString() {
        return "CustomDataHolder{" +
                "dataHolder=" + dataHolder +
                ", customDataManager=" + customDataManager +
                '}';
    }
}
