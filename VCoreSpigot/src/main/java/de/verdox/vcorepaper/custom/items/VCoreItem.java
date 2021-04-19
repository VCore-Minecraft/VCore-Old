package de.verdox.vcorepaper.custom.items;

import de.tr7zw.changeme.nbtapi.NBTItem;
import de.verdox.vcorepaper.custom.CustomDataHolder;
import de.verdox.vcorepaper.custom.entities.CustomEntityManager;
import de.verdox.vcorepaper.custom.entities.EntityCustomData;
import de.verdox.vcorepaper.custom.entities.VCoreEntity;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class VCoreItem extends CustomDataHolder<ItemStack, NBTItem> {
    private final CustomItemManager customItemManager;

    public VCoreItem(ItemStack dataHolder, CustomItemManager customItemManager) {
        super(dataHolder);
        this.customItemManager = customItemManager;
    }

    @Override
    protected <T, R extends EntityCustomData<T>> R instantiateData(Class<? extends R> customDataType) {
        try {
            return customDataType.getDeclaredConstructor(CustomItemManager.class, VCoreItem.class).newInstance(customItemManager,this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public NBTItem getNBTCompound() {
        return new NBTItem(getDataHolder(),true);
    }
}
