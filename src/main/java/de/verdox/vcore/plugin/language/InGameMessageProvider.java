/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.language;

import de.verdox.vcore.plugin.VCorePlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 28.06.2021 00:00
 */
public class InGameMessageProvider {
    private final VCorePlugin<?, ?> vCorePlugin;
    private final Map<Language, LanguageConfig> languagesConfigs = new HashMap<>();
    private final Map<String, InGameMessage> messages = new ConcurrentHashMap<>();
    private String prefix;

    public InGameMessageProvider(@NotNull VCorePlugin<?, ?> vCorePlugin) {
        this.vCorePlugin = vCorePlugin;
        prefix = "&8[&6" + vCorePlugin.getPluginName() + "&8] &f";
        for (Language value : Language.values()) {
            LanguageConfig languageConfig = new LanguageConfig(vCorePlugin, value.getAbbreviation() + ".yml", "lang", value);
            languageConfig.init();
            languagesConfigs.put(value, languageConfig);
        }
        registerStandardMessages();
    }

    public void setPrefix(@NotNull String prefix) {
        this.prefix = prefix;
    }

    public void registerMessage(@NotNull String messageIdentifier, @NotNull InGameMessage inGameMessage) {
        if (messages.containsKey(messageIdentifier))
            throw new IllegalStateException("messageIdentifier " + messageIdentifier + " already taken");
        messages.put(messageIdentifier, inGameMessage);
        languagesConfigs.forEach((language, languageConfig) -> {
            languageConfig.getConfig().set(messageIdentifier, inGameMessage.getTranslatedMessage(language));
            languageConfig.save();
        });
    }

    public String getMessage(@NotNull String messageIdentifier, @NotNull Language language) {
        if (!messages.containsKey(messageIdentifier))
            throw new IllegalStateException("messageIdentifier " + messageIdentifier + " not found!");
        LanguageConfig languageConfig = languagesConfigs.get(language);
        String message;
        if (languageConfig.getConfig().isSet(messageIdentifier))
            message = languagesConfigs.get(language).getConfig().getString(messageIdentifier);
        else
            message = messages.get(messageIdentifier).getTranslatedMessage(language);
        return prefix + message;
    }

    private void registerStandardMessages() {
        registerMessage("Player.noPermission", language -> {
            switch (language) {
                case GERMAN:
                    return "&cKeine Rechte&7!";
                default:
                    return "&cNo Permissions&7!";
            }
        });
        registerMessage("Player.notOnline", language -> {
            switch (language) {
                case GERMAN:
                    return "&cDieser Spieler ist nicht online&7!";
                default:
                    return "&cThis player is not online&7!";
            }
        });
        registerMessage("Command.onlyConsole", language -> {
            switch (language) {
                case GERMAN:
                    return "&cDieser Befehl ist nur für die Konsole&7!";
                default:
                    return "&cThis command is for console only&7!";
            }
        });
        registerMessage("Command.onlyNumbers", language -> {
            switch (language) {
                case GERMAN:
                    return "&cBitte gib eine Zahl ein&7!";
                default:
                    return "&cPlease provide a number&7!";
            }
        });
        registerMessage("Command.onlyNumbersPositive", language -> {
            switch (language) {
                case GERMAN:
                    return "&cBitte gib eine positive Zahl ein&7!";
                default:
                    return "&cPlease provide a positive number&7!";
            }
        });
        registerMessage("Command.onlyNumbersPositiveAndZero", language -> {
            switch (language) {
                case GERMAN:
                    return "&cBitte gib eine positive Zahl oder 0 ein&7!";
                default:
                    return "&cPlease provide a positive number or 0&7!";
            }
        });
        registerMessage("Command.onlyNumbersNegative", language -> {
            switch (language) {
                case GERMAN:
                    return "&cBitte gib eine negative Zahl ein&7!";
                default:
                    return "&cPlease provide a negative number&7!";
            }
        });
        registerMessage("Command.onlyNumbersNegativeAndZero", language -> {
            switch (language) {
                case GERMAN:
                    return "&cBitte gib eine negative Zahl oder 0 ein&7!";
                default:
                    return "&cPlease provide a negative number or 0&7!";
            }
        });
    }
}
