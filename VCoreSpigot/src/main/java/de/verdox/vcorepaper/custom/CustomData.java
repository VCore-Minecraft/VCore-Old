package de.verdox.vcorepaper.custom;

import de.verdox.vcorepaper.custom.annotation.NBTIdentifier;

import java.util.Objects;

public abstract class CustomData <S,C extends CustomDataHolder<S,?>,R extends CustomDataManager<S,?,?>,T> {

    private final R dataManager;
    private final C customDataHolder;
    private T storedData;

    public CustomData(R dataManager, C customDataHolder){
        this.dataManager = dataManager;
        this.customDataHolder = customDataHolder;

        if(!dataManager.getCustomDataStorage().contains(getClass()))
            dataManager.getvCorePaper().consoleMessage("&eWarning&7: &a"+getClass().getCanonicalName()+" &ehas not been registered to &b"+dataManager.getClass().getCanonicalName()+" &eyet.",false);
    }

    public R getDataManager() {
        return dataManager;
    }

    public C getCustomDataHolder() {
        return customDataHolder;
    }

    public final void setStoredData(T storedData) {
        this.getCustomDataHolder().getNBTCompound().setObject(nbtTag(),storedData);
        this.storedData = storedData;
    }

    public final T getStoredData() {
        if(!this.getCustomDataHolder().getNBTCompound().hasKey(nbtTag()))
            return null;
        this.storedData = (T) this.getCustomDataHolder().getNBTCompound().getObject(nbtTag(),Object.class);
        return storedData;
    }

    public abstract String nbtTag();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomData)) return false;
        CustomData<?, ?, ?, ?> that = (CustomData<?, ?, ?, ?>) o;
        return Objects.equals(getStoredData(), that.getStoredData());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getStoredData());
    }

    public String getNBTKey(){
        NBTIdentifier nbtIdentifier = getClass().getAnnotation(NBTIdentifier.class);
        if(nbtIdentifier == null)
            throw new IllegalStateException("Your CustomData Class "+getClass().getCanonicalName()+" needs to have the NBTIdentifier Annotation set!");
        return nbtIdentifier.nbtKey();
    }
}
