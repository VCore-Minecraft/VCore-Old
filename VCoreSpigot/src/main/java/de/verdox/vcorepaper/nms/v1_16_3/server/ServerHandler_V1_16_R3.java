/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.nms.v1_16_3.server;

import de.verdox.vcorepaper.nms.interfaces.server.NMSServerHandler;
import net.minecraft.server.v1_16_R3.DedicatedServer;
import net.minecraft.server.v1_16_R3.MinecraftServer;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.06.2021 14:54
 */
public class ServerHandler_V1_16_R3 implements NMSServerHandler {
    @Override
    public boolean readPropertySetting(SERVER_PROPERTY_BOOLEAN server_property_boolean) {
        DedicatedServer dedicatedServer = (DedicatedServer) MinecraftServer.getServer();
        switch (server_property_boolean){
            case debug: return dedicatedServer.getDedicatedServerProperties().debug;
            case onlineMode: return dedicatedServer.getDedicatedServerProperties().onlineMode;
            case preventProxyConnections: return dedicatedServer.getDedicatedServerProperties().preventProxyConnections;
            case spawnAnimals: return dedicatedServer.getDedicatedServerProperties().spawnAnimals;
            case spawnNpcs: return dedicatedServer.getDedicatedServerProperties().spawnNpcs;
            case pvp: return dedicatedServer.getDedicatedServerProperties().pvp;
            case allowFlight: return dedicatedServer.getDedicatedServerProperties().allowFlight;
            case forceGamemode: return dedicatedServer.getDedicatedServerProperties().forceGamemode;
            case enforceWhitelist: return dedicatedServer.getDedicatedServerProperties().enforceWhitelist;
            case announcePlayerAchievements: return dedicatedServer.getDedicatedServerProperties().announcePlayerAchievements;
            case enableRcon: return dedicatedServer.getDedicatedServerProperties().enableRcon;
            case hardcore: return dedicatedServer.getDedicatedServerProperties().hardcore;
            case allowNether: return dedicatedServer.getDedicatedServerProperties().allowNether;
            case spawnMonsters: return dedicatedServer.getDedicatedServerProperties().spawnMonsters;
            case snooperEnabled: return dedicatedServer.getDedicatedServerProperties().snooperEnabled;
            case useNativeTransport: return dedicatedServer.getDedicatedServerProperties().useNativeTransport;
            case enableCommandBlock: return dedicatedServer.getDedicatedServerProperties().enableCommandBlock;
            case broadcastRconToOps: return dedicatedServer.getDedicatedServerProperties().broadcastRconToOps;
            case broadcastConsoleToOps: return dedicatedServer.getDedicatedServerProperties().broadcastConsoleToOps;
            case syncChunkWrites: return dedicatedServer.getDedicatedServerProperties().syncChunkWrites;
            case enableJmxMonitoring: return dedicatedServer.getDedicatedServerProperties().enableJmxMonitoring;
            case enableStatus: return dedicatedServer.getDedicatedServerProperties().enableStatus;
        }
        return false;
    }

    @Override
    public String readPropertySetting(SERVER_PROPERTY_STRING server_property_string) {

        DedicatedServer dedicatedServer = (DedicatedServer) MinecraftServer.getServer();
        switch (server_property_string){
            case serverIp: return dedicatedServer.getDedicatedServerProperties().serverIp;
            case resourcePack: return dedicatedServer.getDedicatedServerProperties().resourcePack;
            case motd: return dedicatedServer.getDedicatedServerProperties().motd;
            case levelName: return dedicatedServer.getDedicatedServerProperties().levelName;
            case rconPassword: return dedicatedServer.getDedicatedServerProperties().rconPassword;
            case resourcePackHash: return dedicatedServer.getDedicatedServerProperties().resourcePackHash;
            case resourcePackSha1: return dedicatedServer.getDedicatedServerProperties().resourcePackSha1;
            case textFilteringConfig: return dedicatedServer.getDedicatedServerProperties().textFilteringConfig;
            case rconIp: return dedicatedServer.getDedicatedServerProperties().rconIp;
        }

        return "null";
    }

    @Override
    public int readPropertySetting(SERVER_PROPERTY_INTEGER server_property_integer) {

        DedicatedServer dedicatedServer = (DedicatedServer) MinecraftServer.getServer();
        switch (server_property_integer) {
            case serverPort:
                return dedicatedServer.getDedicatedServerProperties().serverPort;
            case maxBuildHeight:
                return dedicatedServer.getDedicatedServerProperties().maxBuildHeight;
            case queryPort:
                return dedicatedServer.getDedicatedServerProperties().queryPort;
            case rconPort:
                return dedicatedServer.getDedicatedServerProperties().rconPort;
            case spawnProtection:
                return dedicatedServer.getDedicatedServerProperties().spawnProtection;
            case opPermissionLevel:
                return dedicatedServer.getDedicatedServerProperties().opPermissionLevel;
            case functionPermissionLevel:
                return dedicatedServer.getDedicatedServerProperties().functionPermissionLevel;
            case rateLimit:
                return dedicatedServer.getDedicatedServerProperties().rateLimit;
            case viewDistance:
                return dedicatedServer.getDedicatedServerProperties().viewDistance;
            case maxPlayers:
                return dedicatedServer.getDedicatedServerProperties().maxPlayers;
            case networkCompressionThreshold:
                return dedicatedServer.getDedicatedServerProperties().networkCompressionThreshold;
            case maxWorldSize:
                return dedicatedServer.getDedicatedServerProperties().maxWorldSize;
            case entityBroadcastRangePercentage:
                return dedicatedServer.getDedicatedServerProperties().entityBroadcastRangePercentage;
            case playerIdleTimeout:
                return dedicatedServer.getDedicatedServerProperties().playerIdleTimeout.get();
        }

        return 0;
    }
}
