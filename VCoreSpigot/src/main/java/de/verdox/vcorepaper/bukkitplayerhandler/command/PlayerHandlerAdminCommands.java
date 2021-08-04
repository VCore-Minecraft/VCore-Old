/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.bukkitplayerhandler.command;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.command.VCommandCallback;
import de.verdox.vcore.plugin.command.VCoreCommand;
import de.verdox.vcore.plugin.command.callback.CommandCallback;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import de.verdox.vcorepaper.VCorePaper;
import de.verdox.vcorepaper.bukkitplayerhandler.playerdata.PlayerHandlerData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 07.07.2021 02:08
 */
public class PlayerHandlerAdminCommands extends VCoreCommand.VCoreBukkitCommand {
    public PlayerHandlerAdminCommands(VCoreSubsystem.Bukkit subsystem, String commandName) {
        super(subsystem, commandName);
        addCommandCallback("resetPlaytime").withPermission("vcore.admin")
                .askFor("player", VCommandCallback.CommandAskType.PLAYER_ONLINE,"&cPlayer nicht online")
                .commandCallback((commandSender, commandParameters) -> {
                    Player player = commandParameters.getObject(0, Player.class);
                    PlayerHandlerData playerHandlerData = VCorePaper.getInstance().getServices().getPipeline().load(PlayerHandlerData.class,player.getUniqueId(), Pipeline.LoadingStrategy.LOAD_LOCAL);
                    if(playerHandlerData == null)
                        return;
                    playerHandlerData.resetPlayTime();
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&eDie Spielzeit des Spielers &e"+player.getName()+" &ewurde zurückgesetzt"));
                });
    }

    @Override
    protected CommandCallback<CommandSender> commandCallback() {
        return null;
    }
}
