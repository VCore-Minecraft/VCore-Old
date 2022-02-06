/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nms.api.server;

import de.verdox.vcore.nms.NMSHandler;
import de.verdox.vcore.nms.NMSVersion;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcorepaper.impl.plugin.VCorePaperPlugin;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.06.2021 14:09
 */
public interface NMSServerHandler extends NMSHandler {

    Command registerRuntimeCommand(@NotNull VCorePaperPlugin plugin, @NotNull String runtimeCommandIdentifier, @NotNull Consumer<Player> callback);

    boolean unregisterCommand(@NotNull PluginCommand pluginCommand);

    boolean unregisterCommand(@NotNull Plugin plugin, @NotNull String commandName);

    boolean readPropertySetting(SERVER_PROPERTY_BOOLEAN server_property_boolean);

    String readPropertySetting(SERVER_PROPERTY_STRING server_property_string);

    int readPropertySetting(SERVER_PROPERTY_INTEGER server_property_integer);

    enum SERVER_PROPERTY_BOOLEAN {
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

    enum SERVER_PROPERTY_STRING {
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

    enum SERVER_PROPERTY_INTEGER {
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
