package de.verdox.vcorepaper.custom.items;

import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.CustomData;
import de.verdox.vcorepaper.custom.CustomDataHolder;
import de.verdox.vcorepaper.custom.nbtholders.NBTItemHolder;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VCoreItem extends CustomDataHolder<ItemStack, NBTItemHolder, CustomItemManager> {
    private static final String separatorLine = ChatColor.translateAlternateColorCodes('&',"                ");

    public VCoreItem(ItemStack dataHolder, CustomItemManager customItemManager) {
        super(dataHolder,customItemManager);
        if(dataHolder == null || dataHolder.getType().isAir())
            throw new NullPointerException("Stack can't be null or air!");
    }

    public VCoreItem copy(){
        return VCorePaper.getInstance().getCustomItemManager().wrap(getClass(), getDataHolder().clone());
    }

    protected List<String> getItemLore(){
        ItemStack stack = getDataHolder();
        List<String> lore = new ArrayList<>();

        boolean adding = true;

        if(stack.getItemMeta().getLore() != null){
            for (int i = 0; i < stack.getItemMeta().getLore().size(); i++) {
                String line = stack.getItemMeta().getLore().get(i);
                if(line.equals(separatorLine))
                    adding = !adding;
                if(adding)
                    lore.add(ChatColor.translateAlternateColorCodes('&',line));
            }
        }
        return lore;
    }

    public List<String> getLore(){
        return getDataHolder().getItemMeta().getLore();
    }

    public String getDisplayName(){
        return getDataHolder().getI18NDisplayName();
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
            Object object = getCustomData(customData.getClass());
            if(object == null)
                return;
            List<String> customDataLore = customData.asLabel(object.toString());

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
