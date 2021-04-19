package de.verdox.vcorepaper.custom.items;

import de.verdox.vcorepaper.custom.CustomData;
import org.bukkit.inventory.ItemStack;

public abstract class ItemCustomData<T> extends CustomData<ItemStack, VCoreItem, CustomItemManager,T> {
    public ItemCustomData(CustomItemManager dataManager, VCoreItem customDataHolder) {
        super(dataManager, customDataHolder);
    }
}
