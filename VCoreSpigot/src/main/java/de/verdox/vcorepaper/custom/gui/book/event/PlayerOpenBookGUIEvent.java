/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.gui.book.event;

import de.verdox.vcorepaper.custom.gui.book.BookGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 14.09.2021 00:09
 */
public class PlayerOpenBookGUIEvent extends BookGUIEvent implements Cancellable {

    private boolean cancelled;

    public PlayerOpenBookGUIEvent(@NotNull Player player, @NotNull BookGUI bookGUI) {
        super(player, bookGUI);
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean cancel) {

    }
}
