package de.verdox.vcorepaper.custom.items;



import de.tr7zw.changeme.nbtapi.NBTItem;
import de.verdox.vcorepaper.custom.CustomData;
import de.verdox.vcorepaper.custom.CustomDataHolder;
import de.verdox.vcorepaper.custom.nbtholders.NBTItemHolder;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class VCoreItem extends CustomDataHolder<ItemStack, NBTItemHolder, CustomItemManager> {
    private static final String separatorLine = ChatColor.translateAlternateColorCodes('&',"                ");

    public VCoreItem(ItemStack dataHolder, CustomItemManager customItemManager) {
        super(dataHolder,customItemManager);
    }

    public List<String> getItemLore(){
        ItemStack stack = getDataHolder();
        List<String> lore = new ArrayList<>();

        if(stack.getItemMeta().getLore() != null){
            for (int i = 0; i < stack.getItemMeta().getLore().size(); i++) {
                String line = stack.getItemMeta().getLore().get(i);
                if(line.equals(separatorLine))
                    break;
                lore.add(ChatColor.translateAlternateColorCodes('&',line));
            }
        }
        return lore;
    }

    private void updateLore(){
        ItemStack stack = getDataHolder();
        ItemMeta meta = getDataHolder().getItemMeta();

        List<String> lore = new ArrayList<>(getItemLore());
        lore.add(separatorLine);

        getCustomDataKeys().forEach(nbtKey -> {
            ItemCustomData<?> customData = getCustomDataManager().getDataType(nbtKey);
            if(customData == null)
                return;
            List<String> customDataLore = customData.asLabel(Objects.requireNonNull(getCustomData(customData.getClass())).toString());

            if(customDataLore != null && !customDataLore.isEmpty())
                lore.addAll(customDataLore.stream().map(line -> ChatColor.translateAlternateColorCodes('&',line)).collect(Collectors.toList()));
        });
        meta.setLore(lore);
        stack.setItemMeta(meta);
    }

    @Override
    protected <T, R extends CustomData<T>> void onStoreData(Class<? extends R> customDataType, T value) {
        updateLore();
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
    public NBTItemHolder getNBTCompound() {
        return new NBTItemHolder(getDataHolder(),true);
    }
}
