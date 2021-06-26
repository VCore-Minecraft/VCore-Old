/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.command.callback;

/**
 * Command callback used for VCoreCommands
 * @param <R> Type of sender
 */

public interface CommandCallback<R> {

    boolean executeCommand (R sender, String[] args);

}
