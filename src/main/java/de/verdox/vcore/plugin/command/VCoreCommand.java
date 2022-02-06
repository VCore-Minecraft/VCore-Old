/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.plugin.command;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.command.callback.CommandSuggestionCallback;
import de.verdox.vcore.plugin.subsystem.VCoreSubsystem;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * VCoreCommand which is used to build a Command inside VCore
 *
 * @param <T> Type of VCorePlugin
 * @param <R> Type of CommandSender
 */
public abstract class VCoreCommand<T extends VCorePlugin<?, ?>, R, V extends VCoreCommandCallback<R,?>> {

    protected final T vCorePlugin;
    protected final String commandName;
    protected final Map<Integer, CommandSuggestionCallback<R>> suggestionCallbackCache = new ConcurrentHashMap<>();
    protected final List<V> vCommandCallbacks = new ArrayList<>();
    protected VCoreSubsystem<T> vCoreSubsystem;

    public VCoreCommand(@NotNull T vCorePlugin, String commandName) {
        this.vCorePlugin = vCorePlugin;
        this.commandName = commandName;
        registerCommand();
    }

    public VCoreCommand(@NotNull VCoreSubsystem<T> vCoreSubsystem, String commandName) {
        this.vCorePlugin = vCoreSubsystem.getVCorePlugin();
        this.vCoreSubsystem = vCoreSubsystem;
        this.commandName = commandName;

        if (vCoreSubsystem.isActivated())
            registerCommand();
    }

    public V addCommandCallback(String... commandPath) {
        V callback = instantiateCommandCallback(vCorePlugin,commandPath);
        vCommandCallbacks.add(Objects.requireNonNull(callback));
        return callback;
    }

    protected abstract V instantiateCommandCallback(T plugin, String[] commandPath);

    public T getVCorePlugin() {
        return vCorePlugin;
    }

    protected abstract void registerCommand();

    public String[] suggestEnum(Class<? extends Enum<?>> enumType) {
        return Arrays.stream(enumType.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }

    public String getCommandName() {
        return commandName;
    }
}
