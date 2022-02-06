/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nbt.block;

import de.verdox.vcore.nbt.CustomDataManager;
import de.verdox.vcore.nbt.VCoreNBTModule;
import de.verdox.vcore.nbt.block.data.VBlockCustomData;
import de.verdox.vcore.plugin.SystemLoadable;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcorepaper.impl.plugin.VCorePaperPlugin;
import org.bukkit.block.Block;

import java.lang.reflect.InvocationTargetException;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 23.08.2021 15:16
 */
public class CustomBlockDataManager extends CustomDataManager<Block, VBlockCustomData<?>, de.verdox.vcore.nbt.block.VBlock.BlockBased> implements SystemLoadable {
    public CustomBlockDataManager(VCoreNBTModule vCoreNBTModule, VCorePaperPlugin vCorePlugin) {
        super(vCoreNBTModule, vCorePlugin);
    }

    public de.verdox.vcore.nbt.block.VBlock.BlockBased getVBlock(Block block) {
        return wrap(de.verdox.vcore.nbt.block.VBlock.BlockBased.class, block);
    }

    @Override
    public <U extends de.verdox.vcore.nbt.block.VBlock.BlockBased> U wrap(Class<? extends U> type, Block inputObject) {
        try {
            return type.getDeclaredConstructor(Block.class, CustomBlockDataManager.class).newInstance(inputObject, this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public <U extends de.verdox.vcore.nbt.block.VBlock.BlockBased> U convertTo(Class<? extends U> type, VBlock.BlockBased customData) {
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
