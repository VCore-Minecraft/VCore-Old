package de.verdox.vcore.command.callback;

public interface CommandCallback<R> {

    boolean executeCommand (R sender, String[] args);

}
