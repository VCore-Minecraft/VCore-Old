/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.gui.book.event;

import de.verdox.vcorepaper.custom.events.VCoreHybridEvent;
import de.verdox.vcorepaper.custom.gui.book.BookGUI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 14.09.2021 00:08
 */
public abstract class BookGUIEvent extends VCoreHybridEvent {
    private final Player player;
    private final BookGUI bookGUI;

    public BookGUIEvent(@NotNull Player player, @NotNull BookGUI bookGUI) {
        this.player = player;
        this.bookGUI = bookGUI;
    }

    public Player getPlayer() {
        return player;
    }

    public BookGUI getBookGUI() {
        return bookGUI;
    }
}
