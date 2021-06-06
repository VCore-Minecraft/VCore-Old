/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.command.callback;

/**
 * Command callback used for VCoreCommands
 * @param <R>
 */

public interface CommandCallback<R> {

    boolean executeCommand (R sender, String[] args);

}
