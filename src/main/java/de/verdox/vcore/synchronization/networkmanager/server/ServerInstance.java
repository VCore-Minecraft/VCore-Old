/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.server;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.pipeline.annotations.DataContext;
import de.verdox.vcore.synchronization.pipeline.annotations.DataStorageIdentifier;
import de.verdox.vcore.synchronization.pipeline.annotations.PreloadStrategy;
import de.verdox.vcore.synchronization.pipeline.annotations.VCoreDataProperties;
import de.verdox.vcore.synchronization.pipeline.datatypes.NetworkData;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 09.07.2021 00:29
 */

@DataStorageIdentifier(identifier = "ServerInstance")
@VCoreDataProperties(preloadStrategy = PreloadStrategy.LOAD_ON_NEED, dataContext = DataContext.GLOBAL, cleanOnNoUse = false)
public class ServerInstance extends NetworkData {

    private String serverName;
    public String serverAddress;
    public int serverPort;
    //TODO: VersionTag als Info hinzufügen
    private String serverType;
    private final Map<String, String> infoTags = new ConcurrentHashMap<>();

    public ServerInstance(@NotNull VCorePlugin<?, ?> plugin, @NotNull UUID objectUUID) {
        super(plugin, objectUUID);
    }

    public ServerType getServerType() {
        if (serverType == null)
            return null;
        return ServerType.valueOf(serverType);
    }

    public void setServerType(@NotNull ServerType serverType) {
        this.serverType = serverType.name();
    }

    public String getServerName() {
        return serverName.toLowerCase();
    }

    void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Map<String, String> getInfoTags() {
        return infoTags;
    }
}
