package de.verdox.vcore.command.callback;

import java.util.List;

public interface CommandSuggestionCallback<R> {
    List<String> getSuggestions(R commandSender, String[] args);
}
