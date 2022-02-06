package de.verdox.vcorepaper.impl.command;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.command.VCoreCommandCallback;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.02.2022 21:40
 */
public class VCorePaperCommandCallback extends VCoreCommandCallback<CommandSender, Player> {
    public VCorePaperCommandCallback(@NotNull VCorePlugin<?, ?> plugin, @NotNull String... commandPath) {
        super(plugin, commandPath);
    }

    @Override
    protected boolean hasSenderPermission(CommandSender commandSender, String permissionNode) {
        return commandSender.hasPermission(permissionNode);
    }

    @Override
    protected void sendCommandSenderMessage(CommandSender commandSender, String message) {
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',message));
    }

    @Override
    protected Player getPlayer(String argument) {
        return Bukkit.getPlayer(argument);
    }

    @Override
    protected boolean isSenderPlayer(CommandSender commandSender) {
        return commandSender instanceof Player;
    }

    @Override
    protected boolean isSenderConsole(CommandSender commandSender) {
        return commandSender instanceof ConsoleCommandSender;
    }

    @Override
    protected List<String> getOnlinePlayerNames() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }
}
