/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.command.callback;

import java.util.List;

/**
 * Command Suggestion callback used for suggest commands
 *
 * @param <R> Type of Sender
 */

public interface CommandSuggestionCallback<R> {
    /**
     *
     * @param commandSender Platform dependent CommandSender
     * @param args Command Suggestions
     * @return Returns List of Suggestions
     */
    List<String> getSuggestions(R commandSender, String[] args);
}
