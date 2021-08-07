/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.command;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import de.verdox.vcore.plugin.command.callback.CommandCallback;
import de.verdox.vcore.plugin.command.callback.CommandSuggestionCallback;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import de.verdox.vcore.plugin.wrapper.types.enums.PlayerGameMode;
import de.verdox.vcore.util.VCoreUtil;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * VCoreCommand which is used to build a Command inside VCore
 * @param <T> Type of VCorePlugin
 * @param <R> Type of CommandSender
 */
public abstract class VCoreCommand <T extends VCorePlugin<?,?>, R> {

    protected final T vCorePlugin;
    protected VCoreSubsystem<T> vCoreSubsystem;
    protected final String commandName;
    protected final Map<Integer, CommandSuggestionCallback<R>> suggestionCallbackCache = new ConcurrentHashMap<>();
    protected final List<VCommandCallback> vCommandCallbacks = new ArrayList<>();

    public VCoreCommand(@Nonnull T vCorePlugin, String commandName){
        this.vCorePlugin = vCorePlugin;
        this.commandName = commandName;
        registerCommand();
    }

    public VCoreCommand(@Nonnull VCoreSubsystem<T> vCoreSubsystem, String commandName){
        this.vCorePlugin = vCoreSubsystem.getVCorePlugin();
        this.vCoreSubsystem = vCoreSubsystem;
        this.commandName = commandName;

        if(vCoreSubsystem.isActivated())
            registerCommand();
    }

    public VCommandCallback addCommandCallback(String... commandPath){
        VCommandCallback vCommandCallback = new VCommandCallback(vCorePlugin,commandPath);
        vCommandCallbacks.add(vCommandCallback);
        return vCommandCallback;
    }

    public T getVCorePlugin() {
        return vCorePlugin;
    }

    protected abstract void registerCommand();

    public String[] suggestEnum (Class<? extends Enum<?>> enumType){
        return Arrays.stream(enumType.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }

    public abstract static class VCoreBukkitCommand extends VCoreCommand<VCorePlugin.Minecraft, CommandSender> implements TabExecutor, Listener {

        public VCoreBukkitCommand(VCorePlugin.Minecraft vCorePlugin, String commandName) {
            super(vCorePlugin, commandName);
            vCorePlugin.getServer().getPluginManager().registerEvents(this,vCorePlugin);
        }

        public VCoreBukkitCommand(VCoreSubsystem.Bukkit vCoreSubsystem, String commandName) {
            super(vCoreSubsystem, commandName);
            vCorePlugin.getServer().getPluginManager().registerEvents(this,vCorePlugin);
        }

        public void sendPlayerMessage(Player player, String message){
            VCoreUtil.BukkitUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.CHAT,message);
        }

        @EventHandler
        public void asyncTabComplete(AsyncTabCompleteEvent e){
            List<String> suggest = new ArrayList<>();
            String[] cmdArgs = e.getBuffer().replace("/","").split("");
            String[] args = Arrays.copyOfRange(cmdArgs,1,cmdArgs.length);
            for (VCommandCallback vCommandCallback : vCommandCallbacks) {
                List<String> suggested = vCommandCallback.suggest(e.getSender(), cmdArgs[0], args);
                if(!suggested.isEmpty())
                    suggest.addAll(suggested);
            }
            e.setCompletions(suggest);
        }

        @Override
        public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            vCorePlugin.async(() -> {
                boolean errorMessageSent = false;
                for (VCommandCallback vCommandCallback : vCommandCallbacks) {
                    VCommandCallback.CallbackResponse response = vCommandCallback.onCommand(sender, command, label, args);
                    if(response.errorMessageSent)
                        errorMessageSent = true;
                    if(response.responseType.equals(VCommandCallback.CallbackResponse.ResponseType.SUCCESS))
                        return;
                }
                if(!errorMessageSent) {
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&8<=========== &6"+commandName+" &8===========>"));
                    for (VCommandCallback vCommandCallback : vCommandCallbacks)
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', vCommandCallback.getSuggested(this)));
                    sender.sendMessage("");
                }
            });
            return false;
        }

        @Override
        public final List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            List<String> suggest = new ArrayList<>();
            for (VCommandCallback vCommandCallback : vCommandCallbacks) {
                List<String> suggested = vCommandCallback.suggest(sender, alias, args);
                if(!suggested.isEmpty())
                    suggest.addAll(suggested);
            }
            return suggest;
        }

        @Override
        protected void registerCommand() {
            getVCorePlugin().consoleMessage("&eRegistering Command&7: &b"+getClass().getSimpleName(),false);
            vCorePlugin.getPlugin().getCommand(commandName).setExecutor(this);
            vCorePlugin.getPlugin().getCommand(commandName).setTabCompleter(this);
        }
    }

    public String getCommandName() {
        return commandName;
    }
}
