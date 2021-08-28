/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.events.paper.blockevents.chestevents;

import de.verdox.vcorepaper.custom.events.VCoreEvent;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 19.08.2021 21:02
 */
public class ChestMergeEvent extends VCoreEvent {
    private final Player merger;
    private final Chest chestConnectedTo;
    private final Chest newChest;
    private final DoubleChest doubleChest;

    public ChestMergeEvent(@NotNull Player merger, @NotNull Chest chestConnectedTo, @NotNull Chest newChest, @NotNull DoubleChest doubleChest) {
        this.merger = merger;
        this.chestConnectedTo = chestConnectedTo;
        this.newChest = newChest;
        this.doubleChest = doubleChest;
    }

    public DoubleChest getDoubleChest() {
        return doubleChest;
    }

    public Player getMerger() {
        return merger;
    }

    public Chest getChestConnectedTo() {
        return chestConnectedTo;
    }

    public Chest getNewChest() {
        return newChest;
    }
}
