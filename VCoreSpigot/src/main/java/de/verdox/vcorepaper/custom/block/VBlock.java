/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.block;

import de.verdox.vcorepaper.custom.CustomData;
import de.verdox.vcorepaper.custom.CustomDataHolder;
import de.verdox.vcorepaper.custom.block.flags.VBlockFlag;
import de.verdox.vcorepaper.custom.nbtholders.block.NBTBlock;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.08.2021 22:11
 */
public class VBlock extends CustomDataHolder<Location, NBTBlock, CustomBlockManager> {
    public VBlock(@Nonnull Location dataHolder, @Nonnull CustomBlockManager customDataManager) {
        super(dataHolder, customDataManager);
    }

    public boolean isFlagAllowed(VBlockFlag flag){
        if(!getNBTCompound().hasKey(flag.getNbtTag()))
            return false;
        return getNBTCompound().getBoolean(flag.getNbtTag());
    }

    public void setBlockFlag(VBlockFlag flag, boolean allowed){
        getNBTCompound().setObject(flag.getNbtTag(),allowed);
    }

    @Override
    protected <T, R extends CustomData<T>> void onStoreData(Class<? extends R> customDataType, T value) {

    }

    @Override
    protected <T, R extends CustomData<T>> R instantiateData(Class<? extends R> customDataType) {
        try {
            return customDataType.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nonnull
    @Override
    public NBTBlock getNBTCompound() {
        return new NBTBlock(getDataHolder());
    }
}
