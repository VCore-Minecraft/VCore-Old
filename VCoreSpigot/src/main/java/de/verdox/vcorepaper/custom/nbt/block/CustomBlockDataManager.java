/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbt.block;

import de.verdox.vcore.plugin.SystemLoadable;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.CustomDataManager;
import de.verdox.vcorepaper.custom.nbt.block.data.VBlockCustomData;
import org.bukkit.block.Block;

import java.lang.reflect.InvocationTargetException;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 23.08.2021 15:16
 */
public class CustomBlockDataManager extends CustomDataManager<Block, VBlockCustomData<?>, VBlock.BlockBased> implements SystemLoadable {
    public CustomBlockDataManager(VCorePaper vCorePaper) {
        super(vCorePaper);
    }

    public VBlock.BlockBased getVBlock(Block block) {
        return wrap(VBlock.BlockBased.class, block);
    }

    @Override
    public <U extends VBlock.BlockBased> U wrap(Class<? extends U> type, Block inputObject) {
        try {
            return type.getDeclaredConstructor(Block.class, CustomBlockDataManager.class).newInstance(inputObject, this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public <U extends VBlock.BlockBased> U convertTo(Class<? extends U> type, VBlock.BlockBased customData) {
        try {
            return type.getDeclaredConstructor(Block.class, CustomBlockDataManager.class).newInstance(customData.getDataHolder(), this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected VBlockCustomData<?> instantiateCustomData(Class<? extends VBlockCustomData<?>> dataClass) {
        try {
            return dataClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public void shutdown() {

    }
}
