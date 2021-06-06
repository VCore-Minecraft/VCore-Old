/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.command.callback;

import java.util.List;

/**
 * Command Suggestion callback used for suggest commands
 * @param <R> Type of Sender
 */

public interface CommandSuggestionCallback<R> {
    List<String> getSuggestions(R commandSender, String[] args);
}
