/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nbt.block;

import de.verdox.vcore.nbt.CustomData;
import de.verdox.vcore.nbt.CustomDataHolder;
import de.verdox.vcore.nbt.CustomDataManager;
import de.verdox.vcore.nbt.block.flags.VBlockFlag;
import de.verdox.vcore.nbt.holders.NBTHolder;
import de.verdox.vcore.nbt.holders.block.NBTBlockHolder;
import de.verdox.vcore.nbt.holders.location.NBTLocation;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

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
    public VBlock(@NotNull D dataHolder, @NotNull M customDataManager) {
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
    public abstract BlockBased asBlockBased();

    /**
     * Gets the Location Storage of this Block
     * Works even if Chunk is not loaded
     * <p>
     * This data probably contains other data
     *
     * @return LocationBased VBLock
     */
    public abstract LocationBased asLocationBased();

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

    public abstract boolean isVBlock();


    public static class LocationBased extends VBlock<Location, NBTLocation, CustomLocationDataManager> {
        private final NBTLocation nbtLocation;

        public LocationBased(@NotNull Location dataHolder, @NotNull CustomLocationDataManager customDataManager) {
            super(dataHolder, customDataManager);
            nbtLocation = new NBTLocation(this, getDataHolder());
        }

        @Override
        public BlockBased asBlockBased() {
            return customDataManager.getVCoreNBTModule().getCustomBlockProvider().getBlockDataManager().getVBlock(getDataHolder().getBlock());
        }

        @Override
        public LocationBased asLocationBased() {
            return this;
        }

        @Override
        public boolean isVBlock() {
            return toNBTHolder().isNBTLocation();
        }

        @NotNull
        @Override
        public NBTLocation toNBTHolder() {
            return nbtLocation;
        }
    }

    public static class BlockBased extends VBlock<Block, NBTBlockHolder, CustomBlockDataManager> {

        @NotNull
        private final Block block;
        private final NBTBlockHolder nbtBlockHolder;

        public BlockBased(@NotNull Block block, @NotNull CustomBlockDataManager customDataManager) {
            super(block, customDataManager);
            this.block = block;
            this.nbtBlockHolder = new NBTBlockHolder(this, block);
        }

        @Override
        public BlockBased asBlockBased() {
            return this;
        }

        @Override
        public LocationBased asLocationBased() {
            return customDataManager.getVCoreNBTModule().getCustomBlockProvider().getLocationDataManager().getVBlock(block.getLocation());
        }

        @Override
        public boolean isVBlock() {
            return toNBTHolder().isNBTBlock();
        }

        @NotNull
        @Override
        public NBTBlockHolder toNBTHolder() {
            return nbtBlockHolder;
        }

        public boolean isFlagSet(VBlockFlag flag) {
            if (!isVBlock())
                return false;
            if (!toNBTHolder().getPersistentDataContainer().hasKey(flag.getNbtTag()))
                return false;
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
