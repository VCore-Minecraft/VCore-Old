package de.verdox.vcorepaper.custom;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.verdox.vcorepaper.custom.entities.EntityCustomData;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class CustomDataHolder<S, T extends NBTCompound> {

    private final S dataHolder;

    public CustomDataHolder(S dataHolder){
        this.dataHolder = dataHolder;
    }

    public final <T, R extends EntityCustomData<T>> void storeCustomData(Class<? extends R> customDataType, T value, Consumer<R> callback){
        R customData = instantiateData(customDataType);
        if(customData == null)
            return;
        customData.setStoredData(value);
        if(callback == null)
            return;
        callback.accept(customData);
    }
    public final <T, R extends EntityCustomData<T>> T getCustomData(Class<? extends R> customDataClass){
        R customEntity = instantiateData(customDataClass);
        if(customEntity == null)
            return null;
        return Objects.requireNonNull(instantiateData(customDataClass)).getStoredData();
    }
    protected abstract  <T, R extends EntityCustomData<T>> R instantiateData(Class <? extends R> customDataType);

    public S getDataHolder() {
        return dataHolder;
    }

    public abstract T getNBTCompound();
}
