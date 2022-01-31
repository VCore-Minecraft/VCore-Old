/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nms.nmshandler.v_1_17_1.server;

import de.verdox.vcore.nms.nmshandler.api.server.NMSServerHandler;
import de.verdox.vcore.plugin.VCorePlugin;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 15.09.2021 22:21
 */
public class ServerHandler_V1_17_1R1 implements NMSServerHandler {
    @Override
    public Command registerRuntimeCommand(VCorePlugin.@NotNull Minecraft plugin, @NotNull String runtimeCommandIdentifier, @NotNull Consumer<Player> callback) {
        return null;
    }

    @Override
    public boolean unregisterCommand(@NotNull PluginCommand pluginCommand) {
        return false;
    }

    @Override
    public boolean unregisterCommand(@NotNull Plugin plugin, @NotNull String commandName) {
        return false;
    }

    @Override
    public boolean readPropertySetting(SERVER_PROPERTY_BOOLEAN server_property_boolean) {
        DedicatedServer dedicatedServer = (DedicatedServer) MinecraftServer.getServer();
        switch (server_property_boolean) {
            case debug:
                return dedicatedServer.getDedicatedServerProperties().debug;
            case onlineMode:
                return dedicatedServer.getDedicatedServerProperties().a;
            case preventProxyConnections:
                return dedicatedServer.getDedicatedServerProperties().b;
            case spawnAnimals:
                return dedicatedServer.getDedicatedServerProperties().d;
            case spawnNpcs:
                return dedicatedServer.getDedicatedServerProperties().e;
            case pvp:
                return dedicatedServer.getDedicatedServerProperties().f;
            case allowFlight:
                return dedicatedServer.getDedicatedServerProperties().g;
            case forceGamemode:
                return dedicatedServer.getDedicatedServerProperties().l;
            case enforceWhitelist:
                return dedicatedServer.getDedicatedServerProperties().m;
            case announcePlayerAchievements:
                return dedicatedServer.getDedicatedServerProperties().r;
            case enableRcon:
                return dedicatedServer.getDedicatedServerProperties().u;
            case hardcore:
                return dedicatedServer.getDedicatedServerProperties().z;
            case allowNether:
                return dedicatedServer.getDedicatedServerProperties().A;
            case spawnMonsters:
                return dedicatedServer.getDedicatedServerProperties().B;
            case snooperEnabled:
                return dedicatedServer.getDedicatedServerProperties().C;
            case useNativeTransport:
                return dedicatedServer.getDedicatedServerProperties().D;
            case enableCommandBlock:
                return dedicatedServer.getDedicatedServerProperties().E;
            case broadcastRconToOps:
                return dedicatedServer.getDedicatedServerProperties().N;
            case broadcastConsoleToOps:
                return dedicatedServer.getDedicatedServerProperties().O;
            case syncChunkWrites:
                return dedicatedServer.getDedicatedServerProperties().Q;
            case enableJmxMonitoring:
                return dedicatedServer.getDedicatedServerProperties().R;
            case enableStatus:
                return dedicatedServer.getDedicatedServerProperties().S;
        }
        return false;
    }

    @Override
    public String readPropertySetting(SERVER_PROPERTY_STRING server_property_string) {
        DedicatedServer dedicatedServer = (DedicatedServer) MinecraftServer.getServer();
        switch (server_property_string) {
            case serverIp:
                return dedicatedServer.getDedicatedServerProperties().c;
            case resourcePack:
                return dedicatedServer.getDedicatedServerProperties().h;
            case motd:
                return dedicatedServer.getDedicatedServerProperties().k;
            case levelName:
                return dedicatedServer.getDedicatedServerProperties().p;
            case rconPassword:
                return dedicatedServer.getDedicatedServerProperties().w;
            case resourcePackHash:
                return dedicatedServer.getDedicatedServerProperties().x;
            case resourcePackSha1:
                return dedicatedServer.getDedicatedServerProperties().y;
            case textFilteringConfig:
                return dedicatedServer.getDedicatedServerProperties().U;
            case rconIp:
                return dedicatedServer.getDedicatedServerProperties().rconIp;
        }
        return "";
    }

    @Override
    public int readPropertySetting(SERVER_PROPERTY_INTEGER server_property_integer) {
        DedicatedServer dedicatedServer = (DedicatedServer) MinecraftServer.getServer();
        switch (server_property_integer) {
            case serverPort:
                return dedicatedServer.getDedicatedServerProperties().q;
            case maxBuildHeight:
                return 256;
            case queryPort:
                return dedicatedServer.getDedicatedServerProperties().t;
            case rconPort:
                return dedicatedServer.getDedicatedServerProperties().v;
            case spawnProtection:
                return dedicatedServer.getDedicatedServerProperties().F;
            case opPermissionLevel:
                return dedicatedServer.getDedicatedServerProperties().G;
            case functionPermissionLevel:
                return dedicatedServer.getDedicatedServerProperties().H;
            case rateLimit:
                return dedicatedServer.getDedicatedServerProperties().J;
            case viewDistance:
                return dedicatedServer.getDedicatedServerProperties().K;
            case maxPlayers:
                return dedicatedServer.getDedicatedServerProperties().L;
            case networkCompressionThreshold:
                return dedicatedServer.getDedicatedServerProperties().M;
            case maxWorldSize:
                return dedicatedServer.getDedicatedServerProperties().P;
            case entityBroadcastRangePercentage:
                return dedicatedServer.getDedicatedServerProperties().T;
            case playerIdleTimeout:
                return dedicatedServer.getDedicatedServerProperties().V.get();
        }
        return 0;
    }
}
