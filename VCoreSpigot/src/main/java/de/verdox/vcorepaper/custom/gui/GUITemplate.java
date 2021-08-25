/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.gui;

import de.verdox.vcore.plugin.bukkit.BukkitPlugin;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.custom.items.CustomItemManager;
import de.verdox.vcorepaper.custom.items.VCoreItem;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;

import javax.annotation.Nonnull;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class GUITemplate {
    //TODO: Callbacks ans Ende der Function

    public static AnvilGUI.Builder createStringInput(BukkitPlugin bukkitPlugin, Player player, String title, Function<String, AnvilGUI.Response> callback) {
        return new AnvilGUI.Builder()
                .plugin(bukkitPlugin)
                .title(ChatColor.translateAlternateColorCodes('&', title))
                .itemLeft(new ItemStack(Material.GOLD_INGOT))
                .onComplete((player1, s) -> callback.apply(s));
    }

    public static AnvilGUI.Builder createIntegerInputGUI(BukkitPlugin bukkitPlugin, Player player, String title, String notValidNumber, Function<Integer, AnvilGUI.Response> callback) {
        return new AnvilGUI.Builder()
                .plugin(bukkitPlugin)
                .title(ChatColor.translateAlternateColorCodes('&', title))
                .itemLeft(new ItemStack(Material.GOLD_INGOT))
                .onComplete((player1, s) -> {
                    try {
                        Integer number = Integer.parseInt(s);
                        return callback.apply(number);
                    } catch (NumberFormatException e) {
                        return AnvilGUI.Response.text(notValidNumber);
                    }
                });
    }

    public static AnvilGUI.Builder createDoubleInputGUI(Locale parsingLocale, BukkitPlugin bukkitPlugin, Player player, String title, String notValidNumber, Function<Double, AnvilGUI.Response> callback) {
        return new AnvilGUI.Builder()
                .plugin(bukkitPlugin)
                .title(ChatColor.translateAlternateColorCodes('&', title))
                .itemLeft(new ItemStack(Material.GOLD_INGOT))
                .onComplete((player1, s) -> {
                    try {
                        NumberFormat format = NumberFormat.getInstance(parsingLocale);
                        Number number = format.parse(s);
                        return callback.apply(number.doubleValue());
                    } catch (NumberFormatException | ParseException e) {
                        return AnvilGUI.Response.text(notValidNumber);
                    }
                });
    }

    public static void createSelectConfirmationGUI(BukkitPlugin bukkitPlugin, Player player, Function<Boolean, VCoreGUI.Response<?, ?>> callback) {
        VCoreItem yesItem = VCorePaper.getInstance().getCustomItemManager().createItemBuilder(Material.GREEN_STAINED_GLASS_PANE, 1, "&aYes").buildItem();
        VCoreItem noItem = VCorePaper.getInstance().getCustomItemManager().createItemBuilder(Material.RED_STAINED_GLASS_PANE, 1, "&cNo").buildItem();
        createSelectConfirmationGUI(bukkitPlugin, player, "&eAre you sure?", yesItem, noItem, callback);
    }

    public static void createSelectConfirmationGUI(BukkitPlugin bukkitPlugin, Player player, String title, String yesTitle, String noTitle, Function<Boolean, VCoreGUI.Response<?, ?>> callback) {
        VCoreItem yesItem = VCorePaper.getInstance().getCustomItemManager().createItemBuilder(Material.GREEN_STAINED_GLASS_PANE, 1, yesTitle).buildItem();
        VCoreItem noItem = VCorePaper.getInstance().getCustomItemManager().createItemBuilder(Material.RED_STAINED_GLASS_PANE, 1, noTitle).buildItem();
        createSelectConfirmationGUI(bukkitPlugin, player, title, yesItem, noItem, callback);
    }

    public static void createSelectConfirmationGUI(BukkitPlugin bukkitPlugin, Player player, String title, VCoreItem yesItem, VCoreItem noItem, Function<Boolean, VCoreGUI.Response<?, ?>> callback) {
        CustomItemManager customItemManager = VCorePaper.getInstance().getCustomItemManager();
        new VCoreGUI.Builder<String>()
                .plugin(bukkitPlugin)
                .title(ChatColor.translateAlternateColorCodes('&', title))
                .type(InventoryType.HOPPER)
                .content(objectContentBuilder -> {
                    objectContentBuilder.addContent(0, customItemManager.createItemBuilder(Material.BLACK_STAINED_GLASS_PANE, 1, "")
                            .buildItem(), "");
                    objectContentBuilder.addContent(1, noItem, "");
                    objectContentBuilder.addContent(2, customItemManager.createItemBuilder(Material.BLACK_STAINED_GLASS_PANE, 1, "")
                            .buildItem(), "");
                    objectContentBuilder.addContent(3, yesItem, "");
                    objectContentBuilder.addContent(4, customItemManager.createItemBuilder(Material.BLACK_STAINED_GLASS_PANE, 1, "")
                            .buildItem(), "");
                })
                .onItemClick(stringVCoreGUIClick -> {
                    VCoreItem vCoreItem = stringVCoreGUIClick.getClickedItem();
                    if (vCoreItem.getDataHolder().getType().equals(Material.GREEN_STAINED_GLASS_PANE))
                        return callback.apply(true);
                    else
                        return callback.apply(false);
                })
                .open(player);
    }

    public static void selectMaterial(BukkitPlugin bukkitPlugin, Player player, Function<VCoreGUI.VCoreGUIClick<Material>, VCoreGUI.Response<?, ?>> onClick, Runnable backCallback) {
        new SelectionGUI<Material>("&eMaterial Selection", Arrays.asList(Material.values()), bukkitPlugin, 1, 5, ItemStack::new)
                .onClick(onClick)
                .onBack(backCallback)
                .toStringFunc(Enum::name)
                .open(player);
    }

    public static class SelectionGUI<T> {
        private final BukkitPlugin bukkitPlugin;
        private final Function<T, ItemStack> objectToItemStack;
        private final Map<VCoreItem, Function<VCoreGUI.VCoreGUIClick<T>, VCoreGUI.Response<?, ?>>> additionalItems = new HashMap<>();
        private final int rows;
        private final int guiSize;
        private final String title;
        private Collection<T> objectList;
        private int page;
        private Supplier<Collection<T>> listUpdater;
        private String pattern;
        private Function<T, String> toStringFunc;
        private Function<VCoreGUI.VCoreGUIClick<T>, VCoreGUI.Response<?, ?>> clickCallback;
        private Runnable backCallback;
        private VCoreGUI.Builder<T> builder;

        public SelectionGUI(@Nonnull String title, @Nonnull Collection<T> objectList, @Nonnull BukkitPlugin bukkitPlugin, @Positive int page, @Positive int rows, @Nonnull Function<T, ItemStack> objectToItemStack) {
            this.title = title;
            this.objectList = objectList;
            this.bukkitPlugin = bukkitPlugin;
            this.objectToItemStack = objectToItemStack;
            this.page = page;
            this.rows = rows;
            if (rows > 4)
                rows = 4;
            guiSize = (rows * 9) + 9 + 9;
        }

        public SelectionGUI(@Nonnull String title, @Nonnull Collection<T> objectList, @Nonnull BukkitPlugin bukkitPlugin, @Nonnull Function<T, ItemStack> objectToItemStack) {
            this(title, objectList, bukkitPlugin, 1, 4, objectToItemStack);
        }

        public SelectionGUI(@Nonnull String title, @Nonnull Collection<T> objectList, @Nonnull BukkitPlugin bukkitPlugin, @Positive int rows, @Nonnull Function<T, ItemStack> objectToItemStack) {
            this(title, objectList, bukkitPlugin, 1, rows, objectToItemStack);
        }

        public SelectionGUI<T> updateList(Supplier<Collection<T>> listUpdater) {
            this.listUpdater = listUpdater;
            return this;
        }

        public SelectionGUI<T> addItem(VCoreItem vCoreItem, @NonNegative int slot, Function<VCoreGUI.VCoreGUIClick<T>, VCoreGUI.Response<?, ?>> clickCallback) {
            if (vCoreItem == null)
                throw new NullPointerException("VCoreItem can't be null!");
            if (clickCallback == null)
                throw new NullPointerException("clickCallback can't be null!");
            if (slot > guiSize)
                slot = (rows * 9 + (slot % 9)) + 9;

            vCoreItem.toNBTHolder().getPersistentDataContainer().setInteger("vcore_gui_slot", slot);
            additionalItems.put(vCoreItem, clickCallback);
            return this;
        }

        public SelectionGUI<T> onClick(Function<VCoreGUI.VCoreGUIClick<T>, VCoreGUI.Response<?, ?>> clickCallback) {
            this.clickCallback = clickCallback;
            return this;
        }

        public SelectionGUI<T> onBack(Runnable backCallback) {
            this.backCallback = backCallback;
            return this;
        }

        public SelectionGUI<T> toStringFunc(Function<T, String> toStringFunc) {
            this.toStringFunc = toStringFunc;
            return this;
        }

        public SelectionGUI<T> withSearchPattern(String pattern, Function<T, String> toStringFunc) {
            this.pattern = pattern;
            this.toStringFunc = toStringFunc;
            return this;
        }

        private void nextPage() {
            if (page + 1 > getMaxPage())
                page = getMaxPage();
            else
                page++;
        }

        private void lastPage() {
            if (page - 1 <= 0)
                page = 1;
            else
                page--;
        }

        private int getMaxPage() {
            long count = filterWithPattern().count();
            return (int) ((count / rows * 9) + 1);
        }

        private Stream<T> filterWithPattern() {
            return objectList.stream().filter(t -> {
                if (t == null)
                    return false;
                ItemStack stack = objectToItemStack.apply(t);
                if (stack == null || stack.getType().equals(Material.AIR) || stack.getType().isAir())
                    return false;
                if (pattern == null || pattern.isEmpty() || toStringFunc == null)
                    return true;
                String string = toStringFunc.apply(t);
                return string.toLowerCase().contains(pattern.toLowerCase());
            });
        }

        public VCoreGUI.Builder<T> createBuilder() {
            if (builder != null)
                return builder;
            VCoreItem border = VCorePaper.getInstance().getCustomItemManager().createItemBuilder(Material.BLACK_STAINED_GLASS_PANE, 1, "&8").buildItem();
            VCoreItem nextPage = VCorePaper.getInstance().getCustomItemManager().createItemBuilder(Material.PAPER, 1, "&aNext page").buildItem();
            VCoreItem search = VCorePaper.getInstance().getCustomItemManager().createItemBuilder(Material.WRITTEN_BOOK, 1, "&eSearch").buildItem();
            VCoreItem lastPage = VCorePaper.getInstance().getCustomItemManager().createItemBuilder(Material.PAPER, 1, "&cLast page").buildItem();
            VCoreItem back = VCorePaper.getInstance().getCustomItemManager().createItemBuilder(Material.BARRIER, 1, "&cBack").buildItem();

            this.builder = new VCoreGUI.Builder<T>()
                    .plugin(bukkitPlugin)
                    .update()
                    .title(ChatColor.translateAlternateColorCodes('&', title))
                    .type(InventoryType.CHEST)
                    .size(guiSize)
                    .content(contentBuilder -> {
                        if (listUpdater != null)
                            objectList = listUpdater.get();

                        for (int i = 0; i < 9; i++)
                            contentBuilder.addContent(i, border, null);

                        AtomicInteger counter = new AtomicInteger(0);
                        filterWithPattern()
                                .skip((page - 1) * (rows * 9L))
                                .limit(rows * 9L)
                                .forEach(t -> {
                                    ItemStack stack = objectToItemStack.apply(t);
                                    if (stack == null)
                                        return;
                                    if (stack.getType().equals(Material.AIR))
                                        return;
                                    if (stack.getType().isEmpty())
                                        return;
                                    VCoreItem vCoreItem = VCorePaper.getInstance().getCustomItemManager().wrap(VCoreItem.class, stack);
                                    contentBuilder.addContent(counter.getAndIncrement() + 9, vCoreItem, t);
                                });


                        if (backCallback != null)
                            contentBuilder.addContent((rows * 9) + 9, back, null);
                        else
                            contentBuilder.addContent((rows * 9) + 9, border, null);

                        contentBuilder.addContent((rows * 9) + 10, border, null);

                        if (page > 1)
                            contentBuilder.addContent((rows * 9) + 11, lastPage, null);
                        else
                            contentBuilder.addContent((rows * 9) + 11, border, null);

                        contentBuilder.addContent((rows * 9) + 12, border, null);
                        contentBuilder.addContent((rows * 9) + 13, border, null);
                        contentBuilder.addContent((rows * 9) + 14, border, null);

                        if (((page) * ((rows * 9) + 9)) + 1 < objectList.size() || counter.get() >= (rows * 9) - 1)
                            contentBuilder.addContent((rows * 9) + 15, nextPage, null);
                        else
                            contentBuilder.addContent((rows * 9) + 15, border, null);

                        contentBuilder.addContent((rows * 9) + 16, border, null);

                        if (this.toStringFunc != null)
                            contentBuilder.addContent((rows * 9) + 17, search, null);
                        else
                            contentBuilder.addContent((rows * 9) + 17, border, null);


                        additionalItems.forEach((vCoreItem, vCoreItemResponseFunction) -> {
                            if (!vCoreItem.toNBTHolder().getPersistentDataContainer().hasKey("vcore_gui_slot"))
                                return;
                            int slot = vCoreItem.toNBTHolder().getPersistentDataContainer().getInteger("vcore_gui_slot");
                            contentBuilder.removeItem(slot);
                            contentBuilder.addContent(slot, vCoreItem, null);
                        });
                    })
                    .onItemClick(vCoreGUIClick -> {
                        VCoreItem vCoreItem = vCoreGUIClick.getClickedItem();
                        if (additionalItems.containsKey(vCoreItem))
                            return additionalItems.get(vCoreItem).apply(vCoreGUIClick);
                        else if (vCoreItem.equals(nextPage))
                            nextPage();
                        else if (vCoreItem.equals(lastPage))
                            lastPage();
                        else if (vCoreItem.equals(back) && backCallback != null) {
                            backCallback.run();
                            return VCoreGUI.Response.nothing();
                        } else if (vCoreItem.equals(search)) {
                            return VCoreGUI.Response.input(s -> {
                                this.pattern = s;
                                return AnvilGUI.Response.close();
                            });
                        }
                        T object = vCoreGUIClick.getDataInItemStack();
                        if (object != null && clickCallback != null)
                            return clickCallback.apply(vCoreGUIClick);
                        else
                            return VCoreGUI.Response.nothing();
                    });
            return builder;
        }

        public void open(Player player) {
            createBuilder().open(player);
        }
    }
}
