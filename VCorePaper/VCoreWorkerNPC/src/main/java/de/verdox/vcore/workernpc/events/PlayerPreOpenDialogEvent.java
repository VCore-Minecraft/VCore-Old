/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.workernpc.events;

import de.verdox.vcore.events.VCoreHybridEvent;
import de.verdox.vcore.gui.book.DialogBuilder;
import de.verdox.vcore.workernpc.WorkerNPC;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.09.2021 22:25
 */
public class PlayerPreOpenDialogEvent extends VCoreHybridEvent implements Cancellable {
    private final Player player;
    private final WorkerNPC workerNPC;
    private DialogBuilder dialogBuilder;
    private boolean cancelled;

    public PlayerPreOpenDialogEvent(@NotNull Player player, @NotNull WorkerNPC workerNPC, DialogBuilder dialogBuilder) {
        this.player = player;
        this.workerNPC = workerNPC;
        this.dialogBuilder = dialogBuilder;
    }

    public Player getPlayer() {
        return player;
    }

    public WorkerNPC getWorkerNPC() {
        return workerNPC;
    }

    public DialogBuilder getDialogBuilder() {
        return dialogBuilder;
    }

    public void setDialogBuilder(DialogBuilder dialogBuilder) {
        this.dialogBuilder = dialogBuilder;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
