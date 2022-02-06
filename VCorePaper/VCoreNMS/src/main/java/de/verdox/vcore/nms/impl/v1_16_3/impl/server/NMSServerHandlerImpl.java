/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.nms.impl.v1_16_3.impl.server;

import de.verdox.vcore.nms.api.server.NMSServerHandler;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcorepaper.impl.plugin.VCorePaperPlugin;
import net.minecraft.server.v1_16_R3.DedicatedServer;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.06.2021 14:54
 */
public class NMSServerHandlerImpl implements NMSServerHandler {

    private VCorePaperPlugin plugin;

    public NMSServerHandlerImpl(VCorePaperPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public Command registerRuntimeCommand(@Nonnull VCorePaperPlugin plugin, @Nonnull String runtimeCommandIdentifier, @NotNull Consumer<Player> callback) {
        PluginCommand pluginCommand = createCommand(runtimeCommandIdentifier, plugin);
        assert pluginCommand != null;
        getCommandMap().register(plugin.getName(), pluginCommand);
        pluginCommand.setExecutor((sender, command, label, args) -> {
            if (!(sender instanceof Player))
                return false;
            Player player = (Player) sender;
            callback.accept(player);
            return true;
        });
        return pluginCommand;
    }

    @Override
    public boolean unregisterCommand(@NotNull PluginCommand pluginCommand) {
        return unregisterCommand(pluginCommand.getPlugin(), pluginCommand.getName());
    }

    @Override
    public boolean unregisterCommand(@NotNull Plugin plugin, @NotNull String commandName) {
        return getCommandMap().getKnownCommands().remove(plugin.getName() + ":" + commandName) != null;
    }

    private PluginCommand createCommand(String runtimeCommandIdentifier, VCorePaperPlugin plugin) {
        try {
            Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            c.setAccessible(true);
            return c.newInstance(runtimeCommandIdentifier, plugin);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    private CommandMap getCommandMap() {
        try {
            Field f = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            return (SimpleCommandMap) f.get(Bukkit.getPluginManager());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean readPropertySetting(SERVER_PROPERTY_BOOLEAN server_property_boolean) {
        DedicatedServer dedicatedServer = (DedicatedServer) MinecraftServer.getServer();
        switch (server_property_boolean) {
            case debug:
                return dedicatedServer.getDedicatedServerProperties().debug;
            case onlineMode:
                return dedicatedServer.getDedicatedServerProperties().onlineMode;
            case preventProxyConnections:
                return dedicatedServer.getDedicatedServerProperties().preventProxyConnections;
            case spawnAnimals:
                return dedicatedServer.getDedicatedServerProperties().spawnAnimals;
            case spawnNpcs:
                return dedicatedServer.getDedicatedServerProperties().spawnNpcs;
            case pvp:
                return dedicatedServer.getDedicatedServerProperties().pvp;
            case allowFlight:
                return dedicatedServer.getDedicatedServerProperties().allowFlight;
            case forceGamemode:
                return dedicatedServer.getDedicatedServerProperties().forceGamemode;
            case enforceWhitelist:
                return dedicatedServer.getDedicatedServerProperties().enforceWhitelist;
            case announcePlayerAchievements:
                return dedicatedServer.getDedicatedServerProperties().announcePlayerAchievements;
            case enableRcon:
                return dedicatedServer.getDedicatedServerProperties().enableRcon;
            case hardcore:
                return dedicatedServer.getDedicatedServerProperties().hardcore;
            case allowNether:
                return dedicatedServer.getDedicatedServerProperties().allowNether;
            case spawnMonsters:
                return dedicatedServer.getDedicatedServerProperties().spawnMonsters;
            case snooperEnabled:
                return dedicatedServer.getDedicatedServerProperties().snooperEnabled;
            case useNativeTransport:
                return dedicatedServer.getDedicatedServerProperties().useNativeTransport;
            case enableCommandBlock:
                return dedicatedServer.getDedicatedServerProperties().enableCommandBlock;
            case broadcastRconToOps:
                return dedicatedServer.getDedicatedServerProperties().broadcastRconToOps;
            case broadcastConsoleToOps:
                return dedicatedServer.getDedicatedServerProperties().broadcastConsoleToOps;
            case syncChunkWrites:
                return dedicatedServer.getDedicatedServerProperties().syncChunkWrites;
            case enableJmxMonitoring:
                return dedicatedServer.getDedicatedServerProperties().enableJmxMonitoring;
            case enableStatus:
                return dedicatedServer.getDedicatedServerProperties().enableStatus;
        }
        return false;
    }

    @Override
    public String readPropertySetting(SERVER_PROPERTY_STRING server_property_string) {

        DedicatedServer dedicatedServer = (DedicatedServer) MinecraftServer.getServer();
        switch (server_property_string) {
            case serverIp:
                return dedicatedServer.getDedicatedServerProperties().serverIp;
            case resourcePack:
                return dedicatedServer.getDedicatedServerProperties().resourcePack;
            case motd:
                return dedicatedServer.getDedicatedServerProperties().motd;
            case levelName:
                return dedicatedServer.getDedicatedServerProperties().levelName;
            case rconPassword:
                return dedicatedServer.getDedicatedServerProperties().rconPassword;
            case resourcePackHash:
                return dedicatedServer.getDedicatedServerProperties().resourcePackHash;
            case resourcePackSha1:
                return dedicatedServer.getDedicatedServerProperties().resourcePackSha1;
            case textFilteringConfig:
                return dedicatedServer.getDedicatedServerProperties().textFilteringConfig;
            case rconIp:
                return dedicatedServer.getDedicatedServerProperties().rconIp;
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
