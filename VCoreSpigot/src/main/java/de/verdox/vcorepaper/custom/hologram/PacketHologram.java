/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.hologram;

import de.verdox.vcorepaper.VCorePaper;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.index.qual.NonNegative;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 23.06.2021 02:23
 */
public class PacketHologram implements HologramInterface{

    private final Location location;
    private final boolean global;
    private final List<Integer> lines = new ArrayList<>();

    public PacketHologram(Location location, boolean global){
        this.location = location.clone();
        this.global = global;
    }

    private Location getLine(@NonNegative int line){
        return location.clone().add(0,1.2 * (1-line),0);
    }

    @Override
    public HologramInterface addTextLine(String line) {
        int lineCount = lines.size();
        return this;
    }

    @Override
    public HologramInterface addItemLine(ItemStack stack) {
        return this;
    }

    @Override
    public HologramInterface clearLines() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void delete() {

    }

    @Override
    public boolean isDeleted() {
        return false;
    }

    @Override
    public HologramInterface showTo(Player player) {
        return null;
    }

    @Override
    public HologramInterface hideFrom(Player player) {
        return null;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public HologramInterface withUpdater(Consumer<HologramContent> consumer, long intervalInTicks) {
        return null;
    }

    @Override
    public HologramInterface withLifetime(long lifetime) {
        return null;
    }

    @Override
    public void spawnHologram() {

    }
}
