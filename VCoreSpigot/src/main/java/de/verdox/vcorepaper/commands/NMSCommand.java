/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.commands;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.command.VCommandCallback;
import de.verdox.vcore.plugin.command.VCoreCommand;
import de.verdox.vcorepaper.VCorePaper;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.06.2021 15:15
 */
public class NMSCommand extends VCoreCommand.VCoreBukkitCommand {
    public NMSCommand(VCorePlugin.Minecraft vCorePlugin, String commandName) {
        super(vCorePlugin, commandName);

        addCommandCallback("world")
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .addCommandPath("sendFakeDimension")
                .askFor("DimensionName", VCommandCallback.CommandAskType.STRING, "&cDimension not found", "NORMAL", "NETHER", "THE_END")
                .commandCallback((commandSender, commandParameters) -> {
                    String env = commandParameters.getObject(0, String.class);
                    Player player = (Player) commandSender;
                    try {
                        VCorePaper.getInstance().getNmsManager().getNmsWorldHandler().sendFakeDimension((Player) commandSender, World.Environment.valueOf(env));
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aDimension successfully changed!"));
                    } catch (IllegalArgumentException e) {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cDimension not found"));
                    }
                })
        ;

        addCommandCallback("world")
                .setExecutor(VCommandCallback.CommandExecutorType.PLAYER)
                .addCommandPath("createDragonBattle")
                .commandCallback((commandSender, commandParameters) -> {
                    Player player = (Player) commandSender;
                    VCorePaper.getInstance().sync(() -> {
                        VCorePaper.getInstance().getNmsManager().getNmsWorldHandler().createDragonBattle(player.getLocation().clone().add(0, 30, 0), player.getLocation().clone());
                    });
                });
    }
}
