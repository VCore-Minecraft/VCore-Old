/*
 * Copyright (c) 2022. Lukas Jonsson
 */

package de.verdox.vcore.plugin.command;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.synchronization.networkmanager.player.VCorePlayer;
import de.verdox.vcore.synchronization.networkmanager.server.ServerInstance;
import de.verdox.vcore.synchronization.networkmanager.server.ServerType;
import de.verdox.vcore.synchronization.pipeline.parts.Pipeline;
import de.verdox.vcore.util.VCoreUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 27.06.2021 00:59
 */
//TODO: Bei CommandCallback CommandSender direkt in VCorePlayer umwandelbar machen
public class VCommandCallback {
    private final VCorePlugin<?, ?> plugin;
    private final List<CommandCallbackInfo> callbackInfos = new ArrayList<>();
    private final String[] commandPath;
    private String neededPermission;
    private CommandExecutorType commandExecutorType;
    private BiConsumer<CommandSender, CommandParameters> providedArguments;
    private boolean restAsString = false;

    //TODO: Im Command Callback eine Möglichkeit dem Spieler vorzeitig die Error Message zu senden

    public VCommandCallback(@NotNull VCorePlugin<?, ?> plugin, @NotNull String... commandPath) {
        this.plugin = plugin;
        this.commandPath = commandPath;
        for (String s : commandPath)
            addCommandPath(s);
    }

    public String getNeededPermission() {
        return neededPermission;
    }

    public VCommandCallback addCommandPath(@NotNull String commandPath) {
        if (commandPath.isEmpty())
            return this;
        int index = callbackInfos.size();
        callbackInfos.add(new CommandPath(plugin, index, commandPath));
        return this;
    }

    public VCommandCallback askFor(@NotNull String name, @NotNull CommandAskType commandAskType, @NotNull String errorMessage, @NotNull String... suggested) {
        int index = callbackInfos.size();
        callbackInfos.add(new CommandAskParameter(plugin, index, name, commandAskType, errorMessage, () -> Arrays.asList(suggested)));
        if (commandAskType.equals(CommandAskType.REST_OF_INPUT))
            restAsString = true;
        return this;
    }

    public VCommandCallback askFor(@NotNull String name, @NotNull CommandAskType commandAskType, @NotNull String errorMessage, @NotNull Supplier<List<String>> supplier) {
        int index = callbackInfos.size();
        callbackInfos.add(new CommandAskParameter(plugin, index, name, commandAskType, errorMessage, supplier));
        if (commandAskType.equals(CommandAskType.REST_OF_INPUT))
            restAsString = true;
        return this;
    }

    //TODO: Permission in Bukkit registrieren
    public VCommandCallback withPermission(@NotNull String permission) {
        this.neededPermission = permission;
        return this;
    }

    public VCommandCallback setExecutor(@NotNull CommandExecutorType commandExecutorType) {
        this.commandExecutorType = commandExecutorType;
        return this;
    }

    public VCommandCallback commandCallback(@NotNull BiConsumer<CommandSender, CommandParameters> providedArguments) {
        this.providedArguments = providedArguments;
        return this;
    }

    String getSuggested(VCoreCommand<?, ?> command) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("&7/&b").append(command.getCommandName()).append(" ");
        for (CommandCallbackInfo callbackInfo : callbackInfos)
            stringBuilder.append(callbackInfo.commandHelpPlaceholder()).append(" ");
        return stringBuilder.toString();
    }

    CommandCallbackInfo getCallbackInfo(int index) {
        if (index < 0 || index >= callbackInfos.size())
            return null;
        return callbackInfos.get(index);
    }

    List<String> suggest(CommandSender sender, String label, String[] args) {
        List<String> suggested = new ArrayList<>();
        // First check if command path is right
        for (int i = 0; i < args.length - 1; i++) {
            String argument = args[i];
            if (callbackInfos.size() <= i)
                return suggested;
            CommandCallbackInfo info = callbackInfos.get(i);
            if (info instanceof CommandPath) {
                if (!argument.equalsIgnoreCase(((CommandPath) info).getCommandPath()))
                    return suggested;
            }
        }
        int currentArgument = args.length - 1;
        if (callbackInfos.size() <= currentArgument)
            return suggested;
        if (neededPermission != null && !neededPermission.isEmpty() && !sender.hasPermission(neededPermission))
            return suggested;
        if (currentArgument == -1)
            return List.of("");
        String argument = args[currentArgument];
        CommandCallbackInfo info = callbackInfos.get(currentArgument);
        return info.suggest(argument);
    }

    CallbackResponse onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != callbackInfos.size() && !restAsString)
            return new CallbackResponse(CallbackResponse.ResponseType.FAILURE, false);
        if (restAsString && args.length == 0)
            return new CallbackResponse(CallbackResponse.ResponseType.FAILURE, false);
        if (this.providedArguments == null)
            return new CallbackResponse(CallbackResponse.ResponseType.FAILURE, false);
        List<Object> providedArguments = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            String argument = args[i];
            CommandCallbackInfo info = callbackInfos.get(i);
            if (info instanceof CommandPath) {
                if (!argument.equalsIgnoreCase(((CommandPath) info).getCommandPath()))
                    return new CallbackResponse(CallbackResponse.ResponseType.FAILURE, false);
            } else if (info instanceof CommandAskParameter) {
                CommandAskParameter commandAskParameter = (CommandAskParameter) info;

                if (commandAskParameter.getCommandAskType().name().contains("PLAYER")) {
                    if (commandAskParameter.getCommandAskType().equals(CommandAskType.PLAYER_ONLINE)) {
                        Player player = Bukkit.getPlayer(argument);
                        if (player == null) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', commandAskParameter.errorMessage));
                            return new CallbackResponse(CallbackResponse.ResponseType.FAILURE, true);
                        }
                        providedArguments.add(player);
                    } else if (commandAskParameter.getCommandAskType().equals(CommandAskType.VCORE_PLAYER)) {
                        VCorePlayer vCorePlayer = plugin.getCoreInstance().getServices().getPipeline().getLocalCache().getAllData(VCorePlayer.class).stream().filter(vCorePlayer1 -> vCorePlayer1.getDisplayName().equalsIgnoreCase(argument)).findAny().orElse(null);
                        if (vCorePlayer == null)
                            return new CallbackResponse(CallbackResponse.ResponseType.FAILURE, true);
                        providedArguments.add(vCorePlayer);
                    }
                } else if (commandAskParameter.getCommandAskType().name().contains("NUMBER")) {

                    try {
                        Double number = Double.parseDouble(argument);

                        if (commandAskParameter.getCommandAskType().equals(CommandAskType.NEGATIVE_NUMBER)) {
                            if (number >= 0) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', commandAskParameter.errorMessage));
                                return new CallbackResponse(CallbackResponse.ResponseType.FAILURE, true);
                            }
                        } else if (commandAskParameter.getCommandAskType().equals(CommandAskType.NEGATIVE_NUMBER_AND_ZERO)) {
                            if (number > 0) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', commandAskParameter.errorMessage));
                                return new CallbackResponse(CallbackResponse.ResponseType.FAILURE, true);
                            }
                        } else if (commandAskParameter.getCommandAskType().equals(CommandAskType.POSITIVE_NUMBER)) {
                            if (number <= 0) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', commandAskParameter.errorMessage));
                                return new CallbackResponse(CallbackResponse.ResponseType.FAILURE, true);
                            }
                        } else if (commandAskParameter.getCommandAskType().equals(CommandAskType.POSITIVE_NUMBER_AND_ZERO)) {
                            if (number < 0) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', commandAskParameter.errorMessage));
                                return new CallbackResponse(CallbackResponse.ResponseType.FAILURE, true);
                            }
                        }

                        providedArguments.add(number);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', commandAskParameter.errorMessage));
                        return new CallbackResponse(CallbackResponse.ResponseType.FAILURE, true);
                    }
                } else if (commandAskParameter.getCommandAskType().equals(CommandAskType.BOOLEAN)) {
                    if (!argument.equalsIgnoreCase("true") && !argument.equalsIgnoreCase("false")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', commandAskParameter.errorMessage));
                        return new CallbackResponse(CallbackResponse.ResponseType.FAILURE, true);
                    }
                    providedArguments.add(Boolean.parseBoolean(argument));
                } else if (commandAskParameter.getCommandAskType().equals(CommandAskType.VCORE_GAMESERVER)) {
                    UUID serverUUID = plugin.getCoreInstance().getNetworkManager().getServerCache().getServerUUID(argument);
                    ServerInstance serverInstance = plugin.getCoreInstance().getServices().getPipeline().load(ServerInstance.class, serverUUID, Pipeline.LoadingStrategy.LOAD_PIPELINE);
                    if (serverInstance == null) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', commandAskParameter.errorMessage));
                        return new CallbackResponse(CallbackResponse.ResponseType.FAILURE, true);
                    }
                    providedArguments.add(serverInstance);
                } else if (commandAskParameter.getCommandAskType().equals(CommandAskType.STRING)) {
                    providedArguments.add(argument);
                } else if (commandAskParameter.getCommandAskType().equals(CommandAskType.REST_OF_INPUT)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int j = i; j < args.length; j++)
                        stringBuilder.append(args[j]).append(" ");
                    providedArguments.add(stringBuilder.toString());
                    // Stop the Command Parsing when the rest of the input is parsed as one string
                    break;
                }
            }
        }
        if (commandExecutorType != null) {
            switch (commandExecutorType) {
                case PLAYER: {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cCommand can only be executed by a player&7!"));
                        return new CallbackResponse(CallbackResponse.ResponseType.FAILURE, true);
                    }
                    break;
                }
                case CONSOLE: {
                    if (!(sender instanceof ConsoleCommandSender)) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cCommand can only be executed by the console&7!"));
                        return new CallbackResponse(CallbackResponse.ResponseType.FAILURE, true);
                    }
                    break;
                }
            }
        }
        if (neededPermission != null && !neededPermission.isEmpty()) {
            if (!sender.hasPermission(neededPermission)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cNo permissions&7!"));
                return new CallbackResponse(CallbackResponse.ResponseType.FAILURE, true);
            }
        }
        this.providedArguments.accept(sender, new CommandParameters(providedArguments));
        return new CallbackResponse(CallbackResponse.ResponseType.SUCCESS, false);
    }

    public enum CommandAskType {
        STRING,
        ENUM,
        REST_OF_INPUT,
        NUMBER,
        POSITIVE_NUMBER,
        POSITIVE_NUMBER_AND_ZERO,
        NEGATIVE_NUMBER,
        NEGATIVE_NUMBER_AND_ZERO,
        PLAYER_ONLINE,
        BOOLEAN,
        VCORE_PLAYER,
        VCORE_GAMESERVER,
    }

    public enum CommandExecutorType {
        PLAYER,
        CONSOLE,
        BOTH
    }

    public static class CallbackResponse {
        final ResponseType responseType;
        final boolean errorMessageSent;

        CallbackResponse(ResponseType responseType, boolean errorMessageSent) {
            this.responseType = responseType;
            this.errorMessageSent = errorMessageSent;
        }

        enum ResponseType {
            SUCCESS,
            FAILURE
        }
    }

    public static class CommandParameters {
        private final List<Object> parameters;

        CommandParameters(List<Object> parameters) {
            this.parameters = parameters;
        }

        public <T> T getObject(@NonNegative int index, @NotNull Class<? extends T> type) {
            return type.cast(parameters.get(index));
        }

        public <E extends Enum<?>> E getEnum(@NonNegative int index, Class<? extends E> type) {
            String input = getObject(index, String.class);
            return Arrays.stream(type.getEnumConstants()).filter(anEnum -> anEnum.name().equals(input)).findAny().orElse(null);
        }

        public Class<?> getType(@NonNegative int index) {
            return parameters.get(index).getClass();
        }

        public int size() {
            return parameters.size();
        }
    }

    public static abstract class CommandCallbackInfo {
        protected final VCorePlugin<?, ?> plugin;
        protected final int index;

        CommandCallbackInfo(VCorePlugin<?, ?> plugin, int index) {
            this.plugin = plugin;
            this.index = index;
        }

        @Nullable
        public abstract List<String> suggest(String argument);

        public abstract String commandHelpPlaceholder();
    }

    public static class CommandPath extends CommandCallbackInfo {
        protected final String commandPath;

        CommandPath(VCorePlugin<?, ?> plugin, int index, String commandPath) {
            super(plugin, index);
            this.commandPath = commandPath;
        }

        @Override
        public List<String> suggest(String argument) {
            if (commandPath.contains(argument))
                return List.of(commandPath);
            return null;
        }

        @Override
        public String commandHelpPlaceholder() {
            return commandPath;
        }

        public String getCommandPath() {
            return commandPath;
        }

        @Override
        public String toString() {
            return commandPath;
        }
    }

    public static class CommandAskParameter extends CommandCallbackInfo {
        protected final String name;
        protected final CommandAskType commandAskType;
        private final String errorMessage;
        private final Supplier<List<String>> supplySuggested;

        CommandAskParameter(VCorePlugin<?, ?> plugin, int index, @NotNull String name, @NotNull CommandAskType commandAskType, @NotNull String errorMessage, Supplier<List<String>> supplySuggested) {
            super(plugin, index);
            this.name = name;
            this.commandAskType = commandAskType;
            this.errorMessage = errorMessage;
            this.supplySuggested = supplySuggested;
        }

        public CommandAskType getCommandAskType() {
            return commandAskType;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        @Override
        public List<String> suggest(String argument) {
            List<String> suggested = supplySuggested.get();
            if (commandAskType.equals(CommandAskType.PLAYER_ONLINE) && suggested.isEmpty())
                return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
            if (commandAskType.equals(CommandAskType.BOOLEAN))
                return List.of("true", "false");
            if (commandAskType.equals(CommandAskType.VCORE_PLAYER)) {
                Set<VCorePlayer> players = plugin.getCoreInstance().getServices().getPipeline().getLocalCache().getAllData(VCorePlayer.class);
                return players.stream().filter(Objects::nonNull).map(VCorePlayer::getDisplayName).collect(Collectors.toList());
            }
            if (commandAskType.name().contains("NUMBER")) {
                if (commandAskType.equals(CommandAskType.NEGATIVE_NUMBER))
                    return List.of(-VCoreUtil.getRandomUtil().randomInt(1, 100) + "");
                else if (commandAskType.equals(CommandAskType.NEGATIVE_NUMBER_AND_ZERO))
                    return List.of(-VCoreUtil.getRandomUtil().randomInt(1, 100) + "");
                else if (commandAskType.equals(CommandAskType.NUMBER))
                    return List.of("0");
                else if (commandAskType.equals(CommandAskType.POSITIVE_NUMBER_AND_ZERO))
                    return List.of(VCoreUtil.getRandomUtil().randomInt(1, 100) + "");
                else if (commandAskType.equals(CommandAskType.POSITIVE_NUMBER))
                    return List.of(VCoreUtil.getRandomUtil().randomInt(1, 100) + "");
            }
            if (commandAskType.equals(CommandAskType.VCORE_GAMESERVER))
                return plugin.getCoreInstance().getServices().getPipeline().getLocalCache().getAllData(ServerInstance.class).stream().filter(serverInstance -> serverInstance.getServerType().equals(ServerType.GAME_SERVER)).map(ServerInstance::getServerName).collect(Collectors.toList());

            return suggested.stream().filter(s -> s.contains(argument)).collect(Collectors.toList());
        }

        @Override
        public String commandHelpPlaceholder() {
            return "&8<&e" + name + "&8>";
        }

        @Override
        public String toString() {
            return "CommandAskParameter{" +
                    "commandAskType=" + commandAskType +
                    ", errorMessage='" + errorMessage + '\'' +
                    '}';
        }
    }
}
