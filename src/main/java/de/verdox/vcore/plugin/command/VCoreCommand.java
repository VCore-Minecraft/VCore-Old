/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.command;

import de.verdox.vcore.plugin.command.callback.CommandCallback;
import de.verdox.vcore.plugin.command.callback.CommandSuggestionCallback;
import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import de.verdox.vcore.util.VCoreUtil;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    public VCoreCommand(T vCorePlugin, String commandName){
        this.vCorePlugin = vCorePlugin;
        this.commandName = commandName;
        registerCommand();
    }

    public VCoreCommand(VCoreSubsystem<T> vCoreSubsystem, String commandName){
        this.vCorePlugin = vCoreSubsystem.getVCorePlugin();
        this.vCoreSubsystem = vCoreSubsystem;
        this.commandName = commandName;

        if(vCoreSubsystem.isActivated())
            registerCommand();
    }

    public void addCommandCallback(VCommandCallback vCommandCallback){
        vCommandCallbacks.add(vCommandCallback);
    }

    public VCoreCommand<T,R> addCommandSuggestions(int argNumber, CommandSuggestionCallback<R> commandSuggestionCallback){
        if(argNumber < 0)
            throw new IllegalStateException("ArgNumber must be positive!");
        suggestionCallbackCache.put(argNumber,commandSuggestionCallback);
        return this;
    }

    public T getVCorePlugin() {
        return vCorePlugin;
    }

    protected abstract void registerCommand();

    protected abstract CommandCallback<R> commandCallback();

    public abstract static class VCoreBukkitCommand extends VCoreCommand<VCorePlugin.Minecraft, CommandSender> implements TabExecutor {
        public VCoreBukkitCommand(VCorePlugin.Minecraft vCorePlugin, String commandName) {
            super(vCorePlugin, commandName);
        }

        public VCoreBukkitCommand(VCoreSubsystem.Bukkit vCoreSubsystem, String commandName) {
            super(vCoreSubsystem, commandName);
        }

        public void sendPlayerMessage(Player player, String message){
            VCoreUtil.getBukkitPlayerUtil().sendPlayerMessage(player, ChatMessageType.CHAT,message);
        }

        @Override
        public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            boolean errorMessageSent = false;
            for (VCommandCallback vCommandCallback : vCommandCallbacks) {
                VCommandCallback.CallbackResponse response = vCommandCallback.onCommand(sender, command, label, args);
                if(response.errorMessageSent)
                    errorMessageSent = true;
                if(response.responseType.equals(VCommandCallback.CallbackResponse.ResponseType.SUCCESS))
                    return true;
            }
            if(!errorMessageSent) {
                sender.sendMessage("");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&8<=========== &6"+commandName+" &8===========>"));
                for (VCommandCallback vCommandCallback : vCommandCallbacks)
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', vCommandCallback.getSuggested(this)));
                sender.sendMessage("");
            }
            return false;
        }

        @Override
        public final List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            List<String> suggest = new ArrayList<>();
            for (VCommandCallback vCommandCallback : vCommandCallbacks) {
                List<String> suggested = vCommandCallback.suggest(sender, command, alias, args);
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
