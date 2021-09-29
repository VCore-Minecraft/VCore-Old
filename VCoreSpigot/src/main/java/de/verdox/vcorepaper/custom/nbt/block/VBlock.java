/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.nbt.block;

import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.CustomData;
import de.verdox.vcorepaper.custom.CustomDataHolder;
import de.verdox.vcorepaper.custom.CustomDataManager;
import de.verdox.vcorepaper.custom.nbt.block.flags.VBlockFlag;
import de.verdox.vcorepaper.custom.nbt.holders.NBTHolder;
import de.verdox.vcorepaper.custom.nbt.holders.block.NBTBlockHolder;
import de.verdox.vcorepaper.custom.nbt.holders.location.NBTLocation;
import de.verdox.vcorepaper.custom.util.Serializer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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

    public void alternativeLootItems(@NotNull ItemStack... itemStacks) {
        toNBTHolder().getPersistentDataContainer().setString("vblockAdditionalLoot", Serializer.itemStackArrayToBase64(itemStacks));
    }

    public boolean hasAlternativeLootItems() {
        return toNBTHolder().getPersistentDataContainer().hasKey("vblockAdditionalLoot");
    }

    public ItemStack[] getAdditionalLootItems() {
        if (!hasAlternativeLootItems())
            return new ItemStack[0];
        try {
            return Serializer.itemStackArrayFromBase64(toNBTHolder().getPersistentDataContainer().getString("vblockAdditionalLoot"));
        } catch (IOException e) {
            e.printStackTrace();
            return new ItemStack[0];
        }
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

    public abstract boolean isVBlock();


    public static class LocationBased extends VBlock<Location, NBTLocation, CustomLocationDataManager> {
        private final NBTLocation nbtLocation;

        public LocationBased(@NotNull Location dataHolder, @NotNull CustomLocationDataManager customDataManager) {
            super(dataHolder, customDataManager);
            nbtLocation = new NBTLocation(this, getDataHolder());
        }

        @Override
        public BlockBased asBlockBased() {
            return VCorePaper.getInstance().getCustomBlockManager().getBlockDataManager().getVBlock(getDataHolder().getBlock());
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
            return VCorePaper.getInstance().getCustomLocationDataManager().getVBlock(block.getLocation());
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
