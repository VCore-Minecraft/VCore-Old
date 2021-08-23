/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.block;

import de.verdox.vcore.plugin.SystemLoadable;
import de.verdox.vcorepaper.VCorePaper;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 23.08.2021 15:25
 */
public class CustomBlockProvider implements SystemLoadable {
    private final VCorePaper vCorePaper;
    private final CustomBlockDataManager customBlockDataManager;
    private final CustomLocationDataManager customLocationDataManager;

    public CustomBlockProvider(VCorePaper vCorePaper) {
        this.vCorePaper = vCorePaper;
        this.customBlockDataManager = new CustomBlockDataManager(vCorePaper);
        this.customLocationDataManager = new CustomLocationDataManager(vCorePaper);
    }

    public CustomBlockDataManager getBlockDataManager() {
        return customBlockDataManager;
    }

    public CustomLocationDataManager getLocationDataManager() {
        return customLocationDataManager;
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

    public VCorePaper getVCorePaper() {
        return vCorePaper;
    }
}
