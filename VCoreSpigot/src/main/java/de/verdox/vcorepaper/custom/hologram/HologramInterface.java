/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.hologram;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public interface HologramInterface {

    HologramInterface addTextLine(String line);

    HologramInterface addItemLine(ItemStack stack);

    HologramInterface clearLines();

    int size();

    void delete();

    boolean isDeleted();

    HologramInterface showTo(Player player);

    HologramInterface hideFrom(Player player);

    Location getLocation();

    HologramInterface withUpdater(Consumer<HologramContent> consumer, long intervalInTicks);

    HologramInterface withLifetime(long lifetime);

    void spawnHologram();

}
