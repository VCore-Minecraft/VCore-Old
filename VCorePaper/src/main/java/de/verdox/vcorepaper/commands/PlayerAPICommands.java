/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.commands;

import de.verdox.vcore.plugin.command.VCoreCommandCallback;
import de.verdox.vcore.plugin.wrapper.types.ServerLocation;
import de.verdox.vcore.plugin.wrapper.types.enums.PlayerGameMode;
import de.verdox.vcore.plugin.wrapper.types.enums.PlayerMessageType;
import de.verdox.vcore.synchronization.networkmanager.enums.GlobalProperty;
import de.verdox.vcore.synchronization.networkmanager.player.VCorePlayer;
import de.verdox.vcore.synchronization.networkmanager.server.ServerInstance;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.impl.command.VCorePaperCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 03.08.2021 23:45
 */
public class PlayerAPICommands extends VCorePaperCommand {
    public PlayerAPICommands(VCorePaper vCorePaper, String commandName) {
        super(vCorePaper, commandName);

        addCommandCallback("playerPosition")
                .withPermission("vcore.playerPosition")
                .askFor("playerName", VCoreCommandCallback.CommandAskType.VCORE_PLAYER, "&cSpieler wurde nicht gefunden", Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).toArray(String[]::new))
                .commandCallback((commandSender, commandParameters) -> {
                    try {
                        VCorePlayer vCorePlayer = commandParameters.getObject(0, VCorePlayer.class);
                        ServerLocation serverLocation = vCorePaper.getPlayerAPI().getServerLocation(vCorePlayer).get(10, TimeUnit.SECONDS);
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eFound Player&7: &b" + serverLocation));
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cCould not find PlayerPosition"));
                    }
                });
        addCommandCallback("teleport")
                .withPermission("vcore.teleport")
                .askFor("serverName", VCoreCommandCallback.CommandAskType.VCORE_GAMESERVER, "&cServer wurde nicht gefunden&7!")
                .askFor("worldName", VCoreCommandCallback.CommandAskType.STRING, "&cBitte gebe eine Welt ein&7!", "world")
                .askFor("x", VCoreCommandCallback.CommandAskType.NUMBER, "&cBitte gebe eine X Koordinate an&7!")
                .askFor("y", VCoreCommandCallback.CommandAskType.NUMBER, "&cBitte gebe eine Y Koordinate an&7!")
                .askFor("z", VCoreCommandCallback.CommandAskType.NUMBER, "&cBitte gebe eine Z Koordinate an&7!")
                .setExecutor(VCoreCommandCallback.CommandExecutorType.PLAYER)
                .commandCallback((commandSender, commandParameters) -> {
                    vCorePlugin.async(() -> {
                        Player sender = (Player) commandSender;
                        VCorePlayer vCorePlayer = vCorePaper.getServices().getPipeline().load(VCorePlayer.class, sender.getUniqueId(), Pipeline.LoadingStrategy.LOAD_PIPELINE);
                        ServerInstance serverInstance = commandParameters.getObject(0, ServerInstance.class);

                        ServerLocation serverLocation = new ServerLocation(serverInstance.getServerName()
                                , commandParameters.getObject(1, String.class)
                                , commandParameters.getObject(2, Double.class)
                                , commandParameters.getObject(3, Double.class)
                                , commandParameters.getObject(4, Double.class));

                        vCorePlugin.getCoreInstance().getPlayerAPI().teleport(vCorePlayer, serverLocation);
                    });
                });
        addCommandCallback("teleportTo")
                .withPermission("vcore.teleportTo")
                .askFor("teleportTo", VCoreCommandCallback.CommandAskType.VCORE_PLAYER, "&cSpieler wurde nicht gefunden&7!")
                .setExecutor(VCoreCommandCallback.CommandExecutorType.PLAYER)
                .commandCallback((commandSender, commandParameters) -> {
                    Player player = (Player) commandSender;
                    VCorePlayer target = commandParameters.getObject(0, VCorePlayer.class);
                    VCorePlayer vCorePlayer = getVCorePlugin().getServices().getPipeline().load(VCorePlayer.class, player.getUniqueId(), Pipeline.LoadingStrategy.LOAD_PIPELINE);
                    if (target == null) {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cSpieler wurde nicht gefunden&7!"));
                        return;
                    }
                    vCorePlugin.getCoreInstance().getPlayerAPI().teleport(vCorePlayer, target);
                });
        addCommandCallback("kickPlayer")
                .withPermission("vcore.kickPlayer")
                .askFor("kickWho", VCoreCommandCallback.CommandAskType.VCORE_PLAYER, "&cSpieler wurde nicht gefunden&7!")
                .askFor("kickMsg", VCoreCommandCallback.CommandAskType.REST_OF_INPUT, "")
                .setExecutor(VCoreCommandCallback.CommandExecutorType.PLAYER)
                .commandCallback((commandSender, commandParameters) -> {
                    VCorePlayer victim = commandParameters.getObject(0, VCorePlayer.class);
                    String kickMessage = commandParameters.getObject(1, String.class);

                    vCorePlugin.getCoreInstance().getPlayerAPI().kickPlayer(victim, kickMessage);
                });

        addCommandCallback("setServer")
                .withPermission("vcore.setServer")
                .askFor("PlayerTarget", VCoreCommandCallback.CommandAskType.VCORE_PLAYER, "&cSpieler wurde nicht gefunden&7!")
                .askFor("newServer", VCoreCommandCallback.CommandAskType.VCORE_GAMESERVER, "")
                .setExecutor(VCoreCommandCallback.CommandExecutorType.PLAYER)
                .commandCallback((commandSender, commandParameters) -> {
                    VCorePlayer victim = commandParameters.getObject(0, VCorePlayer.class);
                    ServerInstance serverInstance = commandParameters.getObject(1, ServerInstance.class);
                    vCorePlugin.getCoreInstance().getPlayerAPI().changeServer(victim, serverInstance.getServerName());
                });

        addCommandCallback("sendMessage")
                .withPermission("vcore.sendMessage")
                .askFor("PlayerTarget", VCoreCommandCallback.CommandAskType.VCORE_PLAYER, "&cSpieler wurde nicht gefunden&7!")
                .askFor("message", VCoreCommandCallback.CommandAskType.REST_OF_INPUT, "")
                .setExecutor(VCoreCommandCallback.CommandExecutorType.PLAYER)
                .commandCallback((commandSender, commandParameters) -> {
                    VCorePlayer victim = commandParameters.getObject(0, VCorePlayer.class);
                    String message = commandParameters.getObject(1, String.class);
                    vCorePlugin.getCoreInstance().getPlayerAPI().sendMessage(victim, PlayerMessageType.CHAT, message);
                });

        addCommandCallback("healPlayer")
                .withPermission("vcore.healPlayer")
                .askFor("PlayerTarget", VCoreCommandCallback.CommandAskType.VCORE_PLAYER, "&cSpieler wurde nicht gefunden&7!")
                .setExecutor(VCoreCommandCallback.CommandExecutorType.PLAYER)
                .commandCallback((commandSender, commandParameters) -> {
                    VCorePlayer victim = commandParameters.getObject(0, VCorePlayer.class);
                    vCorePlugin.getCoreInstance().getPlayerAPI().healPlayer(victim);
                });

        addCommandCallback("feedPlayer")
                .withPermission("vcore.feedPlayer")
                .askFor("PlayerTarget", VCoreCommandCallback.CommandAskType.VCORE_PLAYER, "&cSpieler wurde nicht gefunden&7!")
                .setExecutor(VCoreCommandCallback.CommandExecutorType.PLAYER)
                .commandCallback((commandSender, commandParameters) -> {
                    VCorePlayer victim = commandParameters.getObject(0, VCorePlayer.class);
                    vCorePlugin.getCoreInstance().getPlayerAPI().feedPlayer(victim);
                });

        addCommandCallback("setGameMode")
                .withPermission("vcore.setGameMode")
                .askFor("PlayerTarget", VCoreCommandCallback.CommandAskType.VCORE_PLAYER, "&cSpieler wurde nicht gefunden&7!")
                .askFor("String", VCoreCommandCallback.CommandAskType.STRING, "&cGameMode existiert nicht&7!", suggestEnum(PlayerGameMode.class))
                .setExecutor(VCoreCommandCallback.CommandExecutorType.PLAYER)
                .commandCallback((commandSender, commandParameters) -> {
                    VCorePlayer victim = commandParameters.getObject(0, VCorePlayer.class);
                    PlayerGameMode playerGameMode = commandParameters.getEnum(1, PlayerGameMode.class);
                    if (playerGameMode != null)
                        vCorePlugin.getCoreInstance().getPlayerAPI().setGameMode(victim, playerGameMode);
                });

        addCommandCallback("broadCastMessage")
                .withPermission("vcore.broadCastMessage")
                .askFor("message", VCoreCommandCallback.CommandAskType.REST_OF_INPUT, "")
                .setExecutor(VCoreCommandCallback.CommandExecutorType.PLAYER)
                .commandCallback((commandSender, commandParameters) -> {
                    String message = commandParameters.getObject(0, String.class);
                    vCorePlugin.getCoreInstance().getPlayerAPI().broadcastMessage(message, PlayerMessageType.CHAT, GlobalProperty.NETWORK);
                });

        addCommandCallback("clearInventory")
                .withPermission("vcore.clearInventory")
                .askFor("PlayerTarget", VCoreCommandCallback.CommandAskType.VCORE_PLAYER, "&cSpieler wurde nicht gefunden&7!")
                .setExecutor(VCoreCommandCallback.CommandExecutorType.PLAYER)
                .commandCallback((commandSender, commandParameters) -> {
                    VCorePlayer victim = commandParameters.getObject(0, VCorePlayer.class);
                    vCorePlugin.getCoreInstance().getPlayerAPI().clearInventory(victim);
                });
    }
}
