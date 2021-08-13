/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.player;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.pipeline.annotations.*;
import de.verdox.vcore.synchronization.pipeline.datatypes.NetworkData;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 15.06.2021 01:07
 */

@DataStorageIdentifier(identifier = "VCorePlayer")
@VCoreDataContext(preloadStrategy = PreloadStrategy.LOAD_ON_NEED, dataContext = DataContext.GLOBAL, cleanOnNoUse = false)
public class VCorePlayer extends NetworkData {

    @VCorePersistentData
    public String displayName;
    @VCorePersistentData
    public String currentGameServer;
    @VCorePersistentData
    public String currentProxyServer;

    public VCorePlayer(VCorePlugin<?, ?> plugin, UUID objectUUID) {
        super(plugin, objectUUID);
    }

    public String getCurrentGameServer() {
        return currentGameServer;
    }

    public String getCurrentProxyServer() {
        return currentProxyServer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getObjectUUID());
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void onSync(Map<String, Object> dataBeforeSync) {

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onCleanUp() {

    }
}