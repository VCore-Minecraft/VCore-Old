/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.commands;

import de.verdox.vcore.plugin.command.VCommandCallback;
import de.verdox.vcore.plugin.command.VCoreCommand;
import de.verdox.vcore.plugin.command.callback.CommandCallback;
import de.verdox.vcore.synchronization.networkmanager.player.VCorePlayer;
import de.verdox.vcore.synchronization.networkmanager.player.api.querytypes.ServerLocation;
import de.verdox.vcore.synchronization.networkmanager.server.ServerInstance;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import de.verdox.vcorepaper.VCorePaper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 03.08.2021 23:45
 */
public class PlayerAPICommands extends VCoreCommand.VCoreBukkitCommand {
    public PlayerAPICommands(VCorePaper vCorePaper, String commandName) {
        super(vCorePaper, commandName);

        addCommandCallback("playerPosition")
                .withPermission("vcore.playerPosition")
                .askFor("playerName", VCommandCallback.CommandAskType.VCORE_PLAYER,"&cSpieler wurde nicht gefunden", Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).toArray(String[]::new))
                .commandCallback((commandSender, commandParameters) -> {
                    try {
                        VCorePlayer vCorePlayer = commandParameters.getObject(0,VCorePlayer.class);
                        ServerLocation serverLocation = vCorePaper.getPlayerAPI().getServerLocation(vCorePlayer).get(10, TimeUnit.SECONDS);
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&eFound Player&7: &b"+serverLocation));
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cCould not find PlayerPosition"));
                    }
                });
        addCommandCallback("teleport")
                .withPermission("vcore.teleport")
                .askFor("serverName", VCommandCallback.CommandAskType.VCORE_GAMESERVER,"&cServer wurde nicht gefunden&7!")
                .askFor("worldName", VCommandCallback.CommandAskType.STRING,"&cBitte gebe eine Welt ein&7!","world")
                .askFor("x", VCommandCallback.CommandAskType.NUMBER,"&cBitte gebe eine X Koordinate an&7!")
                .askFor("y", VCommandCallback.CommandAskType.NUMBER,"&cBitte gebe eine Y Koordinate an&7!")
                .askFor("z", VCommandCallback.CommandAskType.NUMBER,"&cBitte gebe eine Z Koordinate an&7!")
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .commandCallback((commandSender, commandParameters) -> {
                    vCorePlugin.async(() -> {
                        Player sender = (Player) commandSender;
                        VCorePlayer vCorePlayer = vCorePaper.getServices().getPipeline().load(VCorePlayer.class,sender.getUniqueId(), Pipeline.LoadingStrategy.LOAD_PIPELINE);
                        ServerInstance serverInstance = commandParameters.getObject(0, ServerInstance.class);

                        ServerLocation serverLocation = new ServerLocation();
                        serverLocation.serverName = serverInstance.serverName;
                        serverLocation.worldName = commandParameters.getObject(1,String.class);
                        serverLocation.x = commandParameters.getObject(2,Double.class);
                        serverLocation.y = commandParameters.getObject(3,Double.class);
                        serverLocation.z = commandParameters.getObject(4,Double.class);

                        vCorePlugin.getCoreInstance().getPlayerAPI().teleport(vCorePlayer,serverLocation);
                    });
                });
        addCommandCallback("teleportTo")
                .withPermission("vcore.teleportTo")
                .askFor("teleportTo", VCommandCallback.CommandAskType.VCORE_PLAYER,"&cSpieler wurde nicht gefunden&7!")
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .commandCallback((commandSender, commandParameters) -> {
                    Player player = (Player) commandSender;
                    VCorePlayer target = commandParameters.getObject(0,VCorePlayer.class);
                    VCorePlayer vCorePlayer = getVCorePlugin().getServices().getPipeline().load(VCorePlayer.class,player.getUniqueId(), Pipeline.LoadingStrategy.LOAD_PIPELINE);
                    if(target == null){
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cSpieler wurde nicht gefunden&7!"));
                        return;
                    }
                    vCorePlugin.getCoreInstance().getPlayerAPI().teleport(vCorePlayer,target);
                });
        addCommandCallback("teleportPlayer")
                .withPermission("vcore.teleportPlayer")
                .askFor("teleportWho", VCommandCallback.CommandAskType.VCORE_PLAYER,"&cSpieler wurde nicht gefunden&7!")
                .askFor("teleportTo", VCommandCallback.CommandAskType.VCORE_PLAYER,"&cSpieler wurde nicht gefunden&7!")
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .commandCallback((commandSender, commandParameters) -> {
                    VCorePlayer victim = commandParameters.getObject(0,VCorePlayer.class);
                    VCorePlayer target = commandParameters.getObject(1,VCorePlayer.class);
                    if(victim.getObjectUUID().equals(target.getObjectUUID())){
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cThe players are identical&7!"));
                        return;
                    }

                    vCorePlugin.getCoreInstance().getPlayerAPI().teleport(victim,target);
                });
    }

    @Override
    protected CommandCallback<CommandSender> commandCallback() {
        return null;
    }
}
