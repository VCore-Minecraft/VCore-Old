/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nbt.items;

import de.tr7zw.changeme.nbtapi.NBTItem;
import de.verdox.vcore.nbt.CustomDataManager;
import de.verdox.vcore.nbt.VCoreNBTModule;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcorepaper.impl.plugin.VCorePaperPlugin;
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
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CustomItemManager extends CustomDataManager<ItemStack, de.verdox.vcore.nbt.items.ItemCustomData<?>, de.verdox.vcore.nbt.items.VCoreItem> implements Listener {

    private final ItemPreset itemPreset;

    public CustomItemManager(VCoreNBTModule vCoreNBTModule, VCorePaperPlugin vCorePlugin) {
        super(vCoreNBTModule, vCorePlugin);
        this.itemPreset = new ItemPreset(this);
        Bukkit.getPluginManager().registerEvents(this, vCorePlugin);
    }

    @Override
    public <U extends VCoreItem> U wrap(Class<? extends U> type, ItemStack inputObject) {
        try {
            return type.getDeclaredConstructor(ItemStack.class, CustomItemManager.class).newInstance(inputObject, this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public <U extends VCoreItem> U convertTo(Class<? extends U> type, de.verdox.vcore.nbt.items.VCoreItem customData) {
        try {
            return type.getDeclaredConstructor(ItemStack.class, CustomItemManager.class).newInstance(customData.getDataHolder(), this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected ItemCustomData<?> instantiateCustomData(Class<? extends ItemCustomData<?>> dataClass) {
        try {
            return dataClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ItemPreset getItemPreset() {
        return itemPreset;
    }

    public VCoreItemBuilder createItemBuilder() {
        return new VCoreItemBuilder(this);
    }

    public VCoreItemBuilder createItemBuilder(Material material) {
        return new VCoreItemBuilder(this, material);
    }

    public VCoreItemBuilder createItemBuilder(ItemStack stack) {
        return new VCoreItemBuilder(this, stack);
    }

    public VCoreItemBuilder createItemBuilder(ItemStack stack, String displayName) {
        return new VCoreItemBuilder(this, stack, displayName);
    }

    public VCoreItemBuilder createItemBuilder(ItemStack stack, int amount, String displayName) {
        return new VCoreItemBuilder(this, stack, amount, displayName);
    }

    public VCoreItemBuilder createItemBuilder(Material material, int amount) {
        return new VCoreItemBuilder(this, material, amount);
    }

    public VCoreItemBuilder createItemBuilder(Material material, int amount, String displayName) {
        return new VCoreItemBuilder(this, material, amount, displayName);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        ItemStack stack = e.getCurrentItem();
        if (stack == null || stack.getType().equals(Material.AIR))
            return;
        NBTItem nbtItem = new NBTItem(stack);
        if (nbtItem.hasKey(ItemPreset.VCoreNBTTags.GUI_ITEM_NOT_DRAGGABLE))
            e.setCancelled(true);
    }

    public de.verdox.vcore.nbt.items.VCoreItem getGuiBorderItem() {
        return getItemPreset()
                .getCustomItemManager()
                .createItemBuilder(Material.BLACK_STAINED_GLASS_PANE, 1, "&8").buildItem();
    }

    public de.verdox.vcore.nbt.items.VCoreItem getHelpItem(@NotNull String title) {
        return getVCoreNBTModule()
                .getCustomItemManager()
                .createItemBuilder(Material.WRITTEN_BOOK, 1, title).buildItem();
    }

    public static class VCoreItemBuilder {

        private final CustomItemManager customItemManager;
        private Material material;
        private ItemStack stack;
        private int amount = 1;
        private String displayName;
        private List<String> lore;
        private List<EnchantmentInfo> enchantments;
        private Set<ItemFlag> itemFlags;
        private ItemMeta meta;
        private Map<String, Object> nbtData = new HashMap<>();

        VCoreItemBuilder(CustomItemManager customItemManager) {
            this.customItemManager = customItemManager;
        }

        VCoreItemBuilder(CustomItemManager customItemManager, Material material) {
            this.customItemManager = customItemManager;
            this.material = material;
        }

        VCoreItemBuilder(CustomItemManager customItemManager, Material material, int amount) {
            this.customItemManager = customItemManager;
            this.material = material;
            this.amount = amount;
        }

        VCoreItemBuilder(CustomItemManager customItemManager, Material material, int amount, String displayName) {
            this.customItemManager = customItemManager;
            this.material = material;
            this.amount = amount;
            this.displayName = displayName;
        }

        VCoreItemBuilder(CustomItemManager customItemManager, ItemStack stack) {
            this(customItemManager, stack, stack.getAmount(), stack.getItemMeta() == null ? "" : stack.getItemMeta().getDisplayName());
        }

        VCoreItemBuilder(CustomItemManager customItemManager, ItemStack stack, String displayName) {
            this(customItemManager, stack, stack.getAmount(), displayName);
        }

        VCoreItemBuilder(CustomItemManager customItemManager, ItemStack stack, int amount, String displayName) {
            this.customItemManager = customItemManager;
            this.material = stack.getType();
            this.stack = stack;
            this.amount = amount;
            this.displayName = displayName;
            if (displayName.equals(stack.getI18NDisplayName()) && stack.getItemMeta() != null && !stack.getItemMeta().getDisplayName().isEmpty())
                this.displayName = stack.getItemMeta().getDisplayName();
            this.lore = stack.getLore();
            this.itemFlags = stack.getItemFlags();
            this.meta = stack.getItemMeta();
        }

        public VCoreItemBuilder material(Material material) {
            this.material = material;
            return this;
        }

        public VCoreItemBuilder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public VCoreItemBuilder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public VCoreItemBuilder lore(String... lore) {
            List<String> loreList = new ArrayList<>();
            for (String s : lore)
                loreList.add(ChatColor.translateAlternateColorCodes('&', s));
            if (this.lore == null)
                this.lore = loreList;
            else
                this.lore.addAll(loreList);
            return this;
        }

        public VCoreItemBuilder addNBTData(String key, Object data) {
            if (nbtData == null)
                nbtData = new HashMap<>();
            nbtData.put(key, data);
            return this;
        }

        public VCoreItemBuilder addEnchantment(Enchantment enchantment, int level, boolean ignoreRestrictions) {
            if (enchantments == null)
                enchantments = new ArrayList<>();
            enchantments.add(new EnchantmentInfo(enchantment, level, ignoreRestrictions));
            return this;
        }

        public VCoreItemBuilder addItemFlag(ItemFlag... itemFlag) {
            if (itemFlags == null)
                itemFlags = new HashSet<>();
            itemFlags.addAll(Arrays.asList(itemFlag));
            return this;
        }

        public de.verdox.vcore.nbt.items.VCoreItem buildItem() {
            return buildItem(de.verdox.vcore.nbt.items.VCoreItem.class);
        }

        public <R extends de.verdox.vcore.nbt.items.VCoreItem> VCoreItem buildItem(Class<? extends R> type) {
            if (material == null)
                throw new NullPointerException("Material can't be null!");
            ItemStack itemStack;
            if (this.stack != null)
                itemStack = this.stack.clone();
            else
                itemStack = new ItemStack(material, amount);
            ItemMeta meta = itemStack.getItemMeta();
            if (displayName != null && !displayName.isEmpty())
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
            if (lore != null && !lore.isEmpty())
                meta.setLore(lore);
            if (enchantments != null && !enchantments.isEmpty())
                enchantments.forEach(enchantmentInfo -> addEnchantment(enchantmentInfo.getEnchantment(), enchantmentInfo.getLevel(), enchantmentInfo.isIgnoreRestrictions()));
            if (itemFlags != null && !itemFlags.isEmpty())
                itemFlags.forEach(this::addItemFlag);

            itemStack.setItemMeta(meta);

            NBTItem nbtItem = new NBTItem(itemStack, true);
            if (nbtData != null)
                nbtData.forEach(nbtItem::setObject);

            return customItemManager.wrap(type, itemStack);
        }
    }

    private record EnchantmentInfo(Enchantment enchantment, int level, boolean ignoreRestrictions) {

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
}
