/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.util.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 18.08.2021 01:47
 */
public class BukkitServerUtil {
    public boolean callCancellable(Cancellable cancellable) {
        if (!(cancellable instanceof Event))
            throw new IllegalStateException(cancellable + " not instance of Event");
        Bukkit.getPluginManager().callEvent((Event) cancellable);
        return !cancellable.isCancelled();
    }
}
