/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbt;

import de.verdox.vcorepaper.custom.nbt.holders.NBTHolder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @param <S> DataHolder Class (e.g. ItemStack, Entity)
 * @param <N> NBTCompound (e.g. NBTItem, NBTEntity)
 */
public abstract class CustomDataHolder<S, N extends NBTHolder<?>, C extends CustomDataManager<S, ?, ?>> {

    protected final S dataHolder;
    protected final C customDataManager;

    public CustomDataHolder(@NotNull S dataHolder, @NotNull C customDataManager) {
        this.dataHolder = dataHolder;
        this.customDataManager = customDataManager;
    }

    protected abstract <T, R extends CustomData<T>> void onStoreData(Class<? extends R> customDataType, T value);

    @NotNull
    public final <T, R extends CustomData<T>> CustomDataHolder<S, N, C> storeCustomData(@Nullable Class<? extends R> customDataType, @NotNull T value, @Nullable Consumer<R> callback) {
        R customData = instantiateData(customDataType);
        if (customData == null)
            throw new NullPointerException("CustomData could not be instantiated");
        if (!customDataManager.exists(customData.getNBTKey()))
            throw new IllegalStateException("CustomDataClass " + customDataType + " has not yet been registered in your plugin!");
        customData.storeCustomData(this, value);
        onStoreData(customDataType, value);
        if (callback == null)
            return this;
        callback.accept(customData);
        return this;
    }


    public final <T, R extends CustomData<T>> boolean deleteCustomData(@Nullable Class<? extends R> customDataType, @Nullable Runnable callback) {
        if (!containsCustomData(customDataType))
            return false;
        R customData = instantiateData(customDataType);
        if (customData == null)
            throw new NullPointerException("CustomData could not be instantiated");
        if (!customDataManager.exists(customData.getNBTKey()))
            throw new IllegalStateException("CustomDataClass " + customDataType + " has not yet been registered in your plugin!");
        boolean result = customData.deleteData(this);
        if (callback != null)
            callback.run();
        return result;
    }

    public final <T, R extends CustomData<T>> boolean deleteCustomData(@Nullable Class<? extends R> customDataType) {
        return deleteCustomData(customDataType, null);
    }

    //TODO: Auf NBT typen zurückgreifen für Integer, UUID, ItemStack usw
    @NotNull
    public final <T, R extends CustomData<T>> CustomDataHolder<S, N, C> storeCustomData(@Nullable Class<? extends R> customDataType, @NotNull T value) {
        return storeCustomData(customDataType, value, null);
    }

    //TODO: Auf NBT typen zurückgreifen für Integer, UUID, ItemStack usw
    public final <T, R extends CustomData<T>> T getCustomData(Class<? extends R> customDataClass) {
        R customData = instantiateData(customDataClass);
        if (customData == null)
            throw new NullPointerException("Could not instantiate: " + customDataClass);
        if (!containsCustomData(customDataClass))
            return customData.defaultValue();
        if (!customDataManager.exists(customData.getNBTKey()))
            throw new IllegalStateException("CustomDataClass " + customDataClass + " has not yet been registered in your plugin!");
        return customData.findInDataHolder(this);
    }

    public <T, R extends CustomData<T>> boolean containsCustomData(Class<? extends R> customDataClass) {
        String nbtKey = customDataManager.getNBTKey(customDataClass);
        return toNBTHolder().getPersistentDataContainer().hasKey(nbtKey);
    }

    @NotNull
    public Set<String> getCustomDataKeys() {
        return toNBTHolder().getPersistentDataContainer().getKeys().parallelStream().collect(Collectors.toSet());
    }

    protected abstract <T, R extends CustomData<T>> R instantiateData(Class<? extends R> customDataType);

    public final void sendDebugInformation(CommandSender commandSender) {
        commandSender.sendMessage("");
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eDebugging &7:"));
        for (String customDataKey : getCustomDataKeys()) {
            //Class<? extends CustomData<?>> type = customDataManager.getDataTypeClass(customDataKey);
            //Object foundObject;
            //if(type != null){
            //    foundObject = getCustomData(type);
            //}
            //else {
            Object foundObject = toNBTHolder().getPersistentDataContainer().getObject(customDataKey, Object.class);
            //}
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7>> &e" + customDataKey + "&7: " + (foundObject != null ? foundObject.toString() : "####")));
        }
    }

    @NotNull
    public S getDataHolder() {
        return dataHolder;
    }

    @NotNull
    public abstract N toNBTHolder();

    @NotNull
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
