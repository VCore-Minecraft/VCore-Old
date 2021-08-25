/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.block;

import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.CustomData;
import de.verdox.vcorepaper.custom.CustomDataHolder;
import de.verdox.vcorepaper.custom.CustomDataManager;
import de.verdox.vcorepaper.custom.block.flags.VBlockFlag;
import de.verdox.vcorepaper.custom.nbtholders.NBTHolder;
import de.verdox.vcorepaper.custom.nbtholders.block.NBTBlockHolder;
import de.verdox.vcorepaper.custom.nbtholders.location.NBTLocation;
import org.bukkit.Location;
import org.bukkit.block.Block;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 10.08.2021 22:11
 */
public abstract class VBlock<D, N extends NBTHolder<?>, M extends CustomDataManager<D, ?, ?>> extends CustomDataHolder<D, N, M> {
    public VBlock(@Nonnull D dataHolder, @Nonnull M customDataManager) {
        super(dataHolder, customDataManager);
    }

    /**
     * Gets the Block Storage of this Block
     * Only works if Chunk is loaded
     * <p>
     * This data probably contains other data
     *
     * @return BlockBased VBLock
     */
    public abstract VBlock.BlockBased asBlockBased();

    /**
     * Gets the Location Storage of this Block
     * Works even if Chunk is not loaded
     * <p>
     * This data probably contains other data
     *
     * @return LocationBased VBLock
     */
    public abstract VBlock.LocationBased asLocationBased();

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

    public static class LocationBased extends VBlock<Location, NBTLocation, CustomLocationDataManager> {
        private final NBTLocation nbtLocation;

        public LocationBased(@Nonnull Location dataHolder, @Nonnull CustomLocationDataManager customDataManager) {
            super(dataHolder, customDataManager);
            nbtLocation = new NBTLocation(getDataHolder());
        }

        @Override
        public BlockBased asBlockBased() {
            return VCorePaper.getInstance().getCustomBlockManager().getBlockDataManager().getVBlock(getDataHolder().getBlock());
        }

        @Override
        public LocationBased asLocationBased() {
            return this;
        }

        @Nonnull
        @Override
        public NBTLocation toNBTHolder() {
            return nbtLocation;
        }
    }

    public static class BlockBased extends VBlock<Block, NBTBlockHolder, CustomBlockDataManager> {

        @Nonnull
        private final Block block;
        private final NBTBlockHolder nbtBlockHolder;

        public BlockBased(@Nonnull Block block, @Nonnull CustomBlockDataManager customDataManager) {
            super(block, customDataManager);
            this.block = block;
            this.nbtBlockHolder = new NBTBlockHolder(block);
        }

        @Override
        public BlockBased asBlockBased() {
            return this;
        }

        @Override
        public LocationBased asLocationBased() {
            return VCorePaper.getInstance().getCustomLocationDataManager().getVBlock(block.getLocation());
        }

        @Nonnull
        @Override
        public NBTBlockHolder toNBTHolder() {
            return nbtBlockHolder;
        }

        public boolean isFlagSet(VBlockFlag flag) {
            if (!toNBTHolder().getPersistentDataContainer().hasKey(flag.getNbtTag())) {
                return false;
            }
            return toNBTHolder().getPersistentDataContainer().getBoolean(flag.getNbtTag());
        }

        /**
         * @param flag  The BlockFlag
         * @param state If state = true the flag will be denied
         */
        public void setBlockFlag(VBlockFlag flag, boolean state) {
            toNBTHolder().getPersistentDataContainer().setObject(flag.getNbtTag(), state);
        }

        public Set<VBlockFlag> getSetBlockFlags() {
            return Arrays.stream(VBlockFlag.values()).filter(this::isFlagSet).collect(Collectors.toSet());
        }
    }
}
