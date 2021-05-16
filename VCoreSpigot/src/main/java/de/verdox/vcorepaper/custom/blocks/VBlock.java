package de.verdox.vcorepaper.custom.blocks;

import de.verdox.vcorepaper.custom.CustomData;
import de.verdox.vcorepaper.custom.CustomDataHolder;
import de.verdox.vcorepaper.custom.nbtholders.NBTBlockHolder;
import org.bukkit.block.BlockState;

import java.lang.reflect.InvocationTargetException;

public class VBlock extends CustomDataHolder<BlockState, NBTBlockHolder, VBlockManager> {

    private final BlockPersistentData blockPersistentData;

    VBlock(BlockState dataHolder, VBlockManager customDataManager, BlockPersistentData blockPersistentData) {
        super(dataHolder, customDataManager);
        this.blockPersistentData = blockPersistentData;
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

    @Override
    public NBTBlockHolder getNBTCompound() {
        return new NBTBlockHolder(getDataHolder(), blockPersistentData);
    }
}
