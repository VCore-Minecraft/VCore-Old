/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.items;

import org.bukkit.Material;

public class ItemPreset {

    private final CustomItemManager customItemManager;

    ItemPreset(CustomItemManager customItemManager) {
        this.customItemManager = customItemManager;
    }

    public CustomItemManager getCustomItemManager() {
        return customItemManager;
    }

    public VCoreItem blackGUIBorder() {
        return getCustomItemManager()
                .createItemBuilder(Material.BLACK_STAINED_GLASS_PANE, 1, "&8*")
                .addNBTData(VCoreNBTTags.GUI_ITEM_NOT_DRAGGABLE, "")
                .buildItem();
    }

    public VCoreItem redBackButton() {
        return getCustomItemManager()
                .createItemBuilder(Material.RED_STAINED_GLASS_PANE, 1, "&cZurück")
                .addNBTData(VCoreNBTTags.GUI_ITEM_NOT_DRAGGABLE, "")
                .buildItem();
    }

    public static class VCoreNBTTags {
        public static final String GUI_ITEM_NOT_DRAGGABLE = "GUI_ITEM_NOT_DRAGGABLE";
        public static final String GUI_ITEM_DRAGGABLE = "GUI_ITEM_DRAGGABLE";
        private static final String VCoreItem_CUSTOM = "VCoreItem_CUSTOM";


    }
}
