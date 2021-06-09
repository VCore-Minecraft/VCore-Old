package de.verdox.vcorepaper.commands;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcorepaper.VCorePaper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class ConsoleCommands implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("listPlugins")){
                VCorePaper.getInstance().consoleMessage("&eDebuggable Plugins&7:",false);
                for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                    if(!(plugin instanceof VCorePlugin.Minecraft))
                        continue;
                    VCorePaper.getInstance().consoleMessage("&a"+plugin.getName(),1,false);
                }
                return true;
            }
        }

        else if(args.length == 4){
            if(args[0].equalsIgnoreCase("plugin")){

                String pluginName = args[1];
                Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
                if(plugin == null){
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cPlugin konnte nicht gefunden werden&7!"));
                    return false;
                }

                if(!(plugin instanceof VCorePlugin.Minecraft)){
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cDas Plugin ist kein VCorePlugin&7!"));
                    return false;
                }

                VCorePlugin.Minecraft vCorePlugin = (VCorePlugin.Minecraft) plugin;

                Player player = Bukkit.getPlayer(args[3]);
                if(player == null){
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cPlayer not found&7: &b"+args[3]));
                    return false;
                }

                vCorePlugin.getSessionManager().getSession(player.getUniqueId()).debugToConsole();

                return true;
            }
        }
        sendHelpMessage(sender);
        return false;
    }


    private void sendHelpMessage(CommandSender sender){
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&edebug help"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&edebug listPlugins"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&edebug plugin &8<&bpluginName&8> &eplayer &8<&bplayerName&8>"));
    }
}
