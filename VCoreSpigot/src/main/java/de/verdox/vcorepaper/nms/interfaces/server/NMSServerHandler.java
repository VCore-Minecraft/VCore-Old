/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms.interfaces.server;

import de.verdox.vcorepaper.nms.NMSHandler;
import de.verdox.vcorepaper.nms.NMSVersion;
import de.verdox.vcorepaper.nms.v1_16_3.server.ServerHandler_V1_16_R3;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.06.2021 14:09
 */
public interface NMSServerHandler extends NMSHandler {

    static NMSServerHandler getRightHandler(NMSVersion nmsVersion){
        if(nmsVersion.equals(NMSVersion.V1_16_5)){
            return new ServerHandler_V1_16_R3();
        }
        return null;
    }

    boolean readPropertySetting(SERVER_PROPERTY_BOOLEAN server_property_boolean);
    String readPropertySetting(SERVER_PROPERTY_STRING server_property_string);
    int readPropertySetting(SERVER_PROPERTY_INTEGER server_property_integer);

    enum SERVER_PROPERTY_BOOLEAN{
        debug,
        onlineMode,
        preventProxyConnections,
        spawnAnimals,
        spawnNpcs,
        pvp,
        allowFlight,
        forceGamemode,
        enforceWhitelist,
        announcePlayerAchievements,
        enableRcon,
        hardcore,
        allowNether,
        spawnMonsters,
        snooperEnabled,
        useNativeTransport,
        enableCommandBlock,
        broadcastRconToOps,
        broadcastConsoleToOps,
        syncChunkWrites,
        enableJmxMonitoring,
        enableStatus,
    }
    enum SERVER_PROPERTY_STRING{
        serverIp,
        resourcePack,
        motd,
        levelName,
        rconPassword,
        resourcePackHash,
        resourcePackSha1,
        textFilteringConfig,
        rconIp,
    }
    enum SERVER_PROPERTY_INTEGER{
        serverPort,
        maxBuildHeight,
        queryPort,
        rconPort,
        spawnProtection,
        opPermissionLevel,
        functionPermissionLevel,
        rateLimit,
        viewDistance,
        maxPlayers,
        networkCompressionThreshold,
        maxWorldSize,
        entityBroadcastRangePercentage,
        playerIdleTimeout,
    }
}
