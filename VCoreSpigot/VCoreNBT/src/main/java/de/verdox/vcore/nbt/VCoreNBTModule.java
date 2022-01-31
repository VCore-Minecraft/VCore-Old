/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nbt;

import de.verdox.vcore.modules.VCoreModule;
import de.verdox.vcore.nbt.block.CustomBlockProvider;
import de.verdox.vcore.nbt.entities.CustomEntityManager;
import de.verdox.vcore.nbt.holders.location.LocationNBTFileStorage;
import de.verdox.vcore.nbt.items.CustomItemManager;
import de.verdox.vcore.plugin.VCorePlugin;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 31.01.2022 19:39
 */
public class VCoreNBTModule implements VCoreModule<VCorePlugin.Minecraft> {

    private CustomEntityManager customEntityManager;
    private CustomBlockProvider customBlockProvider;
    private CustomItemManager customItemManager;
    private LocationNBTFileStorage locationNBTFileStorage;

    @Override
    public void enableModule(VCorePlugin.Minecraft plugin) {
        customEntityManager = new CustomEntityManager(this, plugin);
        customBlockProvider = new CustomBlockProvider(this, plugin);
        customItemManager = new CustomItemManager(this, plugin);
        locationNBTFileStorage = new LocationNBTFileStorage(plugin);
    }

    @Override
    public void disableModule() {
        locationNBTFileStorage.shutdown();
        customBlockProvider.shutdown();
    }

    public CustomBlockProvider getCustomBlockProvider() {
        return customBlockProvider;
    }

    public CustomEntityManager getCustomEntityManager() {
        return customEntityManager;
    }

    public CustomItemManager getCustomItemManager() {
        return customItemManager;
    }

    public LocationNBTFileStorage getLocationNBTFileStorage() {
        return locationNBTFileStorage;
    }
}
