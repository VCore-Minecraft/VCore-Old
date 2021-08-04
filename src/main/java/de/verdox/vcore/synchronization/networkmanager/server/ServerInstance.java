/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.synchronization.networkmanager.server;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.pipeline.annotations.*;
import de.verdox.vcore.synchronization.pipeline.datatypes.NetworkData;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 09.07.2021 00:29
 */

@DataStorageIdentifier(identifier = "ServerInstance")
@VCoreDataContext(preloadStrategy = PreloadStrategy.LOAD_ON_NEED, dataContext = DataContext.GLOBAL, cleanOnNoUse = false)
public class ServerInstance extends NetworkData {

    //TODO: VersionTag
    @VCorePersistentData
    private String serverType;
    @VCorePersistentData
    public String serverName;
    @VCorePersistentData
    public String serverAddress;
    @VCorePersistentData
    public int serverPort;

    public ServerInstance(VCorePlugin<?, ?> plugin, UUID objectUUID) {
        super(plugin, objectUUID);
    }

    public ServerType getServerType(){
        if(serverType == null)
            return null;
        return ServerType.valueOf(serverType);
    }

    public void setServerType(@Nonnull ServerType serverType){
        this.serverType = serverType.name();
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
