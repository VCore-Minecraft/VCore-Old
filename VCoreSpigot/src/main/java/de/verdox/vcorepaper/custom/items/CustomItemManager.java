package de.verdox.vcorepaper.custom.items;

import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.CustomDataManager;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class CustomItemManager extends CustomDataManager<ItemStack,VCoreItem,ItemCustomData<?>> {

    private static CustomItemManager instance = null;

    public CustomItemManager(VCorePaper vCorePaper) {
        super(vCorePaper);
        if(instance != null)
            throw new IllegalStateException("There can only be one CustomEntityManager");
        instance = this;
    }

    @Override
    public VCoreItem wrap(Class<? extends VCoreItem> type, ItemStack inputObject) {
        try {
            return type.getDeclaredConstructor(CustomItemManager.class, ItemStack.class).newInstance(this,inputObject);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}
