package de.verdox.vcorepaper.impl.command;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import de.verdox.vcore.plugin.command.VCoreCommand;
import de.verdox.vcore.plugin.command.VCoreCommandCallback;
import de.verdox.vcorepaper.impl.plugin.VCorePaperPlugin;
import de.verdox.vcorepaper.impl.plugin.VCorePaperSubsystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 05.02.2022 21:40
 */
public class VCorePaperCommand extends VCoreCommand<VCorePaperPlugin, CommandSender, VCorePaperCommandCallback> implements TabExecutor, Listener {
    public VCorePaperCommand(@NotNull VCorePaperPlugin vCorePlugin, String commandName) {
        super(vCorePlugin, commandName);
        vCorePlugin.getServer().getPluginManager().registerEvents(this, vCorePlugin);
    }

    public VCorePaperCommand(VCorePaperSubsystem vCoreSubsystem, String commandName) {
        super(vCoreSubsystem, commandName);
        vCorePlugin.getServer().getPluginManager().registerEvents(this, vCorePlugin);
    }

    @Override
    protected VCorePaperCommandCallback instantiateCommandCallback(VCorePaperPlugin plugin, String[] commandPath) {
        return new VCorePaperCommandCallback(plugin, commandPath);
    }

    @Override
    protected void registerCommand() {
        vCorePlugin.getPlugin().getCommand(commandName).setExecutor(this);
        vCorePlugin.getPlugin().getCommand(commandName).setTabCompleter(this);
    }

    @EventHandler
    public void asyncTabComplete(AsyncTabCompleteEvent e) {
        List<String> suggest = new ArrayList<>();
        String[] cmdArgs = e.getBuffer().replace("/", "").split("");
        String[] args = Arrays.copyOfRange(cmdArgs, 1, cmdArgs.length);
        if (args.length == 0)
            return;
        String commandLabel = args[0].toLowerCase(Locale.ROOT);
        if (Bukkit.getCommandMap().getCommand(commandLabel) == null)
            return;
        String permission = Bukkit.getCommandMap().getCommand(commandLabel).getPermission();
        if (permission != null && !permission.isEmpty() && !e.getSender().hasPermission(permission)) {
            e.setCancelled(true);
            return;
        }
        for (VCorePaperCommandCallback vCommandCallback : vCommandCallbacks) {
            List<String> suggested = vCommandCallback.suggest(e.getSender(), args);
            if (suggested != null && !suggested.isEmpty())
                suggest.addAll(suggested);
        }
        e.setCompletions(suggest);
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        vCorePlugin.async(() -> {
            boolean errorMessageSent = false;
            for (VCorePaperCommandCallback vCommandCallback : vCommandCallbacks) {
                VCoreCommandCallback.CallbackResponse response = vCommandCallback.onCommand(sender, args);
                if (response.errorMessageSent())
                    errorMessageSent = true;
                if (response.responseType().equals(VCoreCommandCallback.CallbackResponse.ResponseType.SUCCESS))
                    return;
            }
            if (!errorMessageSent) {
                sender.sendMessage("");
                for (VCorePaperCommandCallback vCommandCallback : vCommandCallbacks) {
                    if (vCommandCallback.getNeededPermission() != null && !vCommandCallback.getNeededPermission().isEmpty() && !sender.hasPermission(vCommandCallback.getNeededPermission()))
                        continue;
                    String suggested = vCommandCallback.getSuggested(this);
                    if (suggested != null)
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', suggested));
                }
                sender.sendMessage("");
            }
        });
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> suggest = new ArrayList<>();
        for (VCorePaperCommandCallback vCommandCallback : vCommandCallbacks) {
            List<String> suggested = vCommandCallback.suggest(commandSender, strings);
            if (suggested != null && !suggested.isEmpty())
                suggest.addAll(suggested);
        }
        return suggest;
    }
}
