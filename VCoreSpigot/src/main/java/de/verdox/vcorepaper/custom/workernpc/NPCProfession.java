/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.workernpc;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.verdox.vcorepaper.custom.gui.book.DialogBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 21.09.2021 21:10
 */
public abstract class NPCProfession {
    protected final NBTCompound dataContainer;
    private final WorkerNPC workerNPC;

    public NPCProfession(@NotNull WorkerNPC workerNPC, @NotNull NBTCompound dataContainer) {
        this.workerNPC = workerNPC;
        this.dataContainer = dataContainer;
    }

    public abstract void addDialogContent(@NotNull DialogBuilder dialogBuilder, @NotNull Player openingPlayer);

    public abstract void onProfessionAdd(@NotNull WorkerNPC workerNPC);

    public abstract void onProfessionRemove(@NotNull WorkerNPC workerNPC);

    public NBTCompound getDataContainer() {
        return dataContainer;
    }

    public WorkerNPC getWorkerNPC() {
        return workerNPC;
    }
}
