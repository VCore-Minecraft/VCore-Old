/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nbt.block;

import de.verdox.vcore.nbt.VCoreNBTModule;
import de.verdox.vcore.nbt.block.data.VBlockCustomData;
import de.verdox.vcore.plugin.SystemLoadable;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcorepaper.impl.plugin.VCorePaperPlugin;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 23.08.2021 15:25
 */
public class CustomBlockProvider implements SystemLoadable {
    private final CustomBlockDataManager customBlockDataManager;
    private final CustomLocationDataManager customLocationDataManager;

    public CustomBlockProvider(VCoreNBTModule vCoreNBTModule, VCorePaperPlugin vCorePlugin) {
        this.customBlockDataManager = new CustomBlockDataManager(vCoreNBTModule, vCorePlugin);
        this.customLocationDataManager = new CustomLocationDataManager(vCoreNBTModule, vCorePlugin);
    }

    public CustomBlockDataManager getBlockDataManager() {
        return customBlockDataManager;
    }

    public CustomLocationDataManager getLocationDataManager() {
        return customLocationDataManager;
    }

    /**
     * Registering custom Data for both block Saving Types
     */
    public final void globalRegisterData(Class<? extends VBlockCustomData<?>> customDataClass) {
        getBlockDataManager().registerData(customDataClass);
        getLocationDataManager().registerData(customDataClass);
    }

    @Override
    public boolean isLoaded() {
        return customBlockDataManager.isLoaded() && customLocationDataManager.isLoaded();
    }

    @Override
    public void shutdown() {
        customBlockDataManager.shutdown();
        customLocationDataManager.shutdown();
    }
}
