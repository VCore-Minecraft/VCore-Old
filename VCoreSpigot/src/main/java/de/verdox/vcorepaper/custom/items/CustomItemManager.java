package de.verdox.vcorepaper.custom.items;

import de.tr7zw.changeme.nbtapi.NBTItem;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.CustomDataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CustomItemManager extends CustomDataManager<ItemStack,VCoreItem,ItemCustomData<?>> implements Listener {

    private static CustomItemManager instance = null;

    private final ItemPreset itemPreset;

    public CustomItemManager(VCorePaper vCorePaper) {
        super(vCorePaper);
        if(instance != null)
            throw new IllegalStateException("There can only be one CustomEntityManager");
        instance = this;
        this.itemPreset = new ItemPreset(this);
        Bukkit.getPluginManager().registerEvents(this,vCorePaper.getPlugin());
    }

    @Override
    public VCoreItem wrap(Class<? extends VCoreItem> type, ItemStack inputObject) {
        try {
            return type.getDeclaredConstructor(ItemStack.class, CustomItemManager.class).newInstance(inputObject,this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ItemPreset getItemPreset() {
        return itemPreset;
    }

    public VCoreItemBuilder createItemBuilder(){
        return new VCoreItemBuilder(this);
    }

    public VCoreItemBuilder createItemBuilder(Material material){
        return new VCoreItemBuilder(this, material);
    }

    public VCoreItemBuilder createItemBuilder(Material material, int amount){
        return new VCoreItemBuilder(this, material, amount);
    }

    public VCoreItemBuilder createItemBuilder(Material material, int amount, String displayName){
        return new VCoreItemBuilder(this, material, amount, displayName);
    }

    public static class VCoreItemBuilder {

        private Material material;
        private int amount = 1;
        private String displayName;

        private List<String> lore;
        private List<EnchantmentInfo> enchantments;
        private Set<ItemFlag> itemFlags;

        private Map<String, Object> nbtData;

        private final CustomItemManager customItemManager;

        VCoreItemBuilder(CustomItemManager customItemManager){
            this.customItemManager = customItemManager;
        }

        VCoreItemBuilder(CustomItemManager customItemManager, Material material){
            this.customItemManager = customItemManager;
            this.material = material;
        }

        VCoreItemBuilder(CustomItemManager customItemManager, Material material, int amount){
            this.customItemManager = customItemManager;
            this.material = material;
            this.amount = amount;
        }

        VCoreItemBuilder(CustomItemManager customItemManager, Material material, int amount, String displayName){
            this.customItemManager = customItemManager;
            this.material = material;
            this.amount = amount;
            this.displayName =  displayName;
        }

        public VCoreItemBuilder material(Material material){
            this.material = material;
            return this;
        }

        public VCoreItemBuilder amount(int amount){
            this.amount = amount;
            return this;
        }

        public VCoreItemBuilder displayName(String displayName){
            this.displayName = displayName;
            return this;
        }

        public VCoreItemBuilder lore(String... lore){
            List<String> loreList = new ArrayList<>();
            for (String s : lore)
                loreList.add(ChatColor.translateAlternateColorCodes('&',s));
            this.lore = loreList;
            return this;
        }

        public VCoreItemBuilder addNBTData(String key, Object data){
            if(nbtData == null)
                nbtData = new HashMap<>();
            nbtData.put(key,data);
            return this;
        }

        public VCoreItemBuilder addEnchantment(Enchantment enchantment, int level, boolean ignoreRestrictions){
            if(enchantments == null)
                enchantments = new ArrayList<>();
            enchantments.add(new EnchantmentInfo(enchantment,level,ignoreRestrictions));
            return this;
        }

        public VCoreItemBuilder addItemFlag(ItemFlag... itemFlag){
            if(itemFlags == null)
                itemFlags = new HashSet<>();
            itemFlags.addAll(Arrays.asList(itemFlag));
            return this;
        }

        public VCoreItem buildItem(){
            return buildItem(VCoreItem.class);
        }

        public <R extends VCoreItem> VCoreItem buildItem(Class<? extends R> type){
            if(material == null)
                throw new NullPointerException("Material can't be null!");
            ItemStack itemStack = new ItemStack(material,amount);
            ItemMeta meta = itemStack.getItemMeta();
            if(displayName != null && !displayName.isEmpty())
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',displayName));
            if(lore != null && !lore.isEmpty())
                meta.setLore(lore);
            if(enchantments != null && !enchantments.isEmpty())
                enchantments.forEach(enchantmentInfo -> addEnchantment(enchantmentInfo.getEnchantment(),enchantmentInfo.getLevel(),enchantmentInfo.isIgnoreRestrictions()));
            if(itemFlags != null && !itemFlags.isEmpty())
                itemFlags.forEach(this::addItemFlag);

            itemStack.setItemMeta(meta);

            NBTItem nbtItem = new NBTItem(itemStack,true);
            if(nbtData != null)
                nbtData.forEach(nbtItem::setObject);

            return customItemManager.wrap(type,itemStack);
        }
    }

    private static class EnchantmentInfo{

        private final Enchantment enchantment;
        private final int level;
        private final boolean ignoreRestrictions;

        EnchantmentInfo(Enchantment enchantment, int level, boolean ignoreRestrictions){
            this.enchantment = enchantment;
            this.level = level;
            this.ignoreRestrictions = ignoreRestrictions;
        }

        public Enchantment getEnchantment() {
            return enchantment;
        }

        public int getLevel() {
            return level;
        }

        public boolean isIgnoreRestrictions() {
            return ignoreRestrictions;
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        ItemStack stack = e.getCurrentItem();
        if(stack == null || stack.getType().equals(Material.AIR))
            return;
        NBTItem nbtItem = new NBTItem(stack);
        if(nbtItem.hasKey(ItemPreset.VCoreNBTTags.GUI_ITEM_NOT_DRAGGABLE))
            e.setCancelled(true);
    }
}